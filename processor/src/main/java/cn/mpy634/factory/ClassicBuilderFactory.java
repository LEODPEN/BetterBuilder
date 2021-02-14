package cn.mpy634.factory;

import cn.mpy634.constant.StrConstant;
import cn.mpy634.utils.JCTreeUtils;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;

/**
 * @author LEO D PEN
 * @date 2021/2/14
 * @desc
 */
public class ClassicBuilderFactory extends BuilderFactory{

    public ClassicBuilderFactory(TreeMaker treeMaker, Names names) {
        super(treeMaker, names);
    }

    @Override
    public void dealRequiredFields(Element e) {
        // ignore or give bad op msg.
    }


    @Override
    public void makeBuilderMethod(JCTree.JCClassDecl innerClass) {
        List<JCTree.JCStatement> statements = List.of(
                treeMaker.Return(
                        treeMaker.NewClass(
                                null,
                                List.nil(),
                                treeMaker.Ident(innerClass.name),
                                List.nil(),
                                null
                        )
                )
        );
        JCTree.JCMethodDecl builder = treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(StrConstant.BUILDER),
                treeMaker.Ident(innerClass.name),
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0L, statements),
                null
        );
        jcClassDecl.defs = jcClassDecl.defs.prepend(builder);
    }

    @Override
    public void makeBuilder() {
        Name builderClassName = names.fromString(jcClassDecl.name.toString() + StrConstant.builderSuffix);
        List<JCTree.JCVariableDecl> builderFields = JCTreeUtils.copyFields(variableDecls, treeMaker, names);
        JCTree.JCMethodDecl constructor = JCTreeUtils.makeNoArgsConstructor(treeMaker, names, false);

        ListBuffer<JCTree.JCMethodDecl> setters = new ListBuffer<>();
        for (JCTree.JCVariableDecl var : builderFields) {
            treeMaker.pos = var.pos;
            setters.append(makeFluentSet(var.name, var.vartype, treeMaker.Ident(builderClassName)));
        }
        JCTree.JCMethodDecl buildMethod = makeBuildMethod(jcClassDecl, builderFields);

        JCTree.JCClassDecl builderClass = makeBuilderClass(builderClassName,
                builderFields,
                constructor,
                setters.toList(),
                buildMethod);
        jcClassDecl.defs = jcClassDecl.defs.append(builderClass);
        makeBuilderMethod(builderClass);
    }

    private JCTree.JCMethodDecl makeBuildMethod(JCTree.JCClassDecl toBuildClass, List<JCTree.JCVariableDecl> variableDecls) {
        JCTree.JCExpression returnType = treeMaker.Ident(toBuildClass.name);
        ListBuffer<JCTree.JCExpression> argsType = new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> args = new ListBuffer<>();
        for (JCTree.JCVariableDecl variableDecl : variableDecls) {
            args.append(JCTreeUtils.makeSelect(StrConstant.THIS, variableDecl.name, treeMaker, names));
            argsType.append(variableDecl.vartype);
        }
        List<JCTree.JCStatement> statements = List.of(
                treeMaker.Return(
                        treeMaker.NewClass(
                                null,
                                argsType.toList(),
                                returnType,
                                args.toList(),
                                null
                        ))
        );
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString(StrConstant.BUILD),
                returnType,
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0L, statements),
                null
        );
    }


    private JCTree.JCClassDecl makeBuilderClass(Name className, List<JCTree.JCVariableDecl> fields,
                                               JCTree.JCMethodDecl constructor, List<JCTree.JCMethodDecl> setters,
                                               JCTree.JCMethodDecl buildMethod) {
        ListBuffer<JCTree> classBody = new ListBuffer<>();
        classBody.appendList(List.convert(JCTree.class, fields))
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
}
