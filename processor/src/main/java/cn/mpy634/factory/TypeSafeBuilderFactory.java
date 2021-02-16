package cn.mpy634.factory;

import cn.mpy634.constant.StrConstant;
import cn.mpy634.utils.ElementUtils;
import cn.mpy634.utils.JCTreeUtils;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;
import java.util.Map;

import static cn.mpy634.utils.JCTreeUtils.memberAccess;

/**
 * @author LEO D PEN
 * @date 2021/2/14
 * @desc
 */
public class TypeSafeBuilderFactory extends BuilderFactory{

    private Map<String, String> generateNameMap;

    public TypeSafeBuilderFactory(TreeMaker treeMaker, Names names) {
        super(treeMaker, names);
    }

    @Override
    public void dealRequiredFields(Element e) {
        generateNameMap = ElementUtils.constructTypeSaleList(e);
    }

    @Override
    public void makeBuilder() {
        // make true builder
        final Name builderClassName = names.fromString(jcClassDecl.name.toString() + StrConstant.builderSuffix);
        List<JCTree.JCVariableDecl> builderFields = JCTreeUtils.copyFields(variableDecls, treeMaker, names);
        JCTree.JCMethodDecl constructor = JCTreeUtils.makeNoArgsConstructor(treeMaker, names, false);
        List<JCTree.JCVariableDecl> generateFields = makeGenerateFields();
        // make setters
        ListBuffer<JCTree.JCMethodDecl> setters = new ListBuffer<>();
        for (JCTree.JCVariableDecl var : builderFields) {
            if (generateNameMap.containsKey(var.name.toString())) {
                setters.append(makeStrongerFluentSet(var.name, var.vartype, treeMaker.Ident(builderClassName)));
            } else {
                setters.append(makeFluentSet(var.name, var.vartype, treeMaker.Ident(builderClassName)));
            }
        }
        // make build inner
        JCTree.JCMethodDecl buildMethod = makeBuildMethod();
        JCTree.JCClassDecl builderClass = makeBuilderClass(builderClassName,
                builderFields,
                generateFields,
                constructor,
                setters.toList(),
                buildMethod);
        jcClassDecl.defs = jcClassDecl.defs.append(builderClass);
        makeBuilderMethod(builderClass);
    }

    private JCTree.JCMethodDecl makeBuildMethod() {
        JCTree.JCExpression returnType = treeMaker.Ident(jcClassDecl.name);

        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        ListBuffer<JCTree.JCExpression> argsType = new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> args = new ListBuffer<>();
        JCTree.JCExpression exception = memberAccess("java.lang.IllegalArgumentException", treeMaker, names);
        for (JCTree.JCVariableDecl variableDecl : variableDecls) {
            Name varName = variableDecl.name;
            args.append(JCTreeUtils.makeSelect(StrConstant.THIS, varName, treeMaker, names));
            argsType.append(variableDecl.vartype);
                if (generateNameMap.containsKey(varName.toString())) {
                    statements.append(
                            treeMaker.If(
                                    treeMaker.Parens(
                                            treeMaker.Binary(
                                                    JCTree.Tag.NE,
                                                    treeMaker.Ident(names.fromString(generateNameMap.get(varName.toString()))),
                                                    treeMaker.Literal(TypeTag.BOOLEAN, 1))
                                    ),
                                    treeMaker.Throw(
                                            treeMaker.NewClass(
                                                    null,
                                                    List.nil(),
                                                    exception,
                                                    List.of(treeMaker.Literal(TypeTag.CLASS, varName + StrConstant.exceptionSuffix)),
                                                    null
                                            )
                                    ),
                                    null
                            )
                    );
            }
        }
         statements.append(treeMaker.Return(
                 treeMaker.NewClass(
                         null,
                         argsType.toList(),
                         returnType,
                         args.toList(),
                         null
                 )));
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString(StrConstant.BUILD),
                returnType,
                List.nil(),
                List.nil(),
                List.of(exception),
                treeMaker.Block(0L, statements.toList()),
                null
        );
    }

    private JCTree.JCMethodDecl makeStrongerFluentSet(Name fieldName, JCTree.JCExpression fieldType, JCTree.JCExpression returnType) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(JCTreeUtils.makeAssignment(
                treeMaker,
                JCTreeUtils.makeSelect(StrConstant.THIS, fieldName, treeMaker, names),
                treeMaker.Ident(fieldName)
        ));
        if (generateNameMap.containsKey(fieldName.toString())) {
            statements.append(JCTreeUtils.makeAssignment(treeMaker,
                    JCTreeUtils.makeSelect(StrConstant.THIS,
                            names.fromString(generateNameMap.get(fieldName.toString())),
                            treeMaker, names),
                    treeMaker.Literal(TypeTag.BOOLEAN, 1)));
        }
        statements.append(treeMaker.Return(treeMaker.Ident(names.fromString(StrConstant.THIS))));

        JCTree.JCBlock block = treeMaker.Block(0L, statements.toList());

        // params
        List<JCTree.JCVariableDecl> params = List.of(
                treeMaker.VarDef(
                        treeMaker.Modifiers(Flags.PARAMETER),
                        fieldName,
                        fieldType, null)
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                fieldName,
                returnType,
                List.nil(),
                params,
                List.nil(),
                block,
                null);
    }

    private JCTree.JCClassDecl makeBuilderClass(Name className, List<JCTree.JCVariableDecl> fields,
                                                List<JCTree.JCVariableDecl> generateFields,
                                                JCTree.JCMethodDecl constructor, List<JCTree.JCMethodDecl> setters,
                                                JCTree.JCMethodDecl buildMethod) {

        ListBuffer<JCTree> classBody = new ListBuffer<>();
        classBody.appendList(List.convert(JCTree.class, fields))
                .appendList(List.convert(JCTree.class, generateFields))
                .append(constructor)
                .appendList(List.convert(JCTree.class, setters))
                .append(buildMethod);
        return treeMaker.ClassDef(
                treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                className,
                List.nil(),
                null,
                List.nil(),
                classBody.toList()
        );

    }

    private List<JCTree.JCVariableDecl> makeGenerateFields() {
        ListBuffer<JCTree.JCVariableDecl> res = new ListBuffer<>();
        for (String n : generateNameMap.values()) {
            JCTree.JCVariableDecl var;
            res.append((var = treeMaker.VarDef(
                    treeMaker.Modifiers(Flags.PRIVATE),
                    names.fromString(n),
                    treeMaker.TypeIdent(TypeTag.BOOLEAN),
                    null
            )));
        }
        return res.toList();
    }
}
