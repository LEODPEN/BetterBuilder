package cn.mpy634.factory;

import cn.mpy634.constant.StrConstant;
import cn.mpy634.utils.JCTreeUtils;

import javax.lang.model.element.Element;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.Set;

/**
 * @author LEO D PEN
 * @date 2021/2/14
 * @desc
 */
public abstract class BuilderFactory {

    protected TreeMaker treeMaker;

    protected Names names;

    protected JCTree.JCClassDecl jcClassDecl;

    protected List<JCTree.JCVariableDecl> variableDecls;

    public BuilderFactory(TreeMaker treeMaker, Names names) {
        this.treeMaker = treeMaker;
        this.names = names;
    }

    public void setJcClassDecl(JCTree.JCClassDecl jcClassDecl) {
        this.jcClassDecl = jcClassDecl;
    }

    public void setVariableDecls(List<JCTree.JCVariableDecl> variableDecls) {
        this.variableDecls = variableDecls;
    }

    public void completeBetterBuilder(boolean makeAllArgsConstructor,
                                      Set<String>[] ignore,
                                      boolean get,
                                      boolean set,
                                      byte setType) {
        makeAgsConstructor(makeAllArgsConstructor);

        makeBuilder();

        makeFluent(ignore, get, set, setType);
    }


    public abstract void dealRequiredFields(Element e);


    public void makeAgsConstructor(boolean flag) {
        if (flag) {
            jcClassDecl.defs = jcClassDecl.defs.prepend(JCTreeUtils.makeAllArgsConstructor(variableDecls, treeMaker, names));
        }
    }

    public abstract void makeBuilder();

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

    private void makeFluent(Set<String>[] ignore, boolean get, boolean set, byte setType) {
        JCTree.JCIdent classType = treeMaker.Ident(jcClassDecl.name);
        ListBuffer<JCTree> methods = new ListBuffer<>();
        for (JCTree.JCVariableDecl variableDecl : variableDecls) {
            Name name = variableDecl.getName();
            treeMaker.pos = variableDecl.pos;
            if (set && !ignore[1].contains(name.toString())) {
                methods.append(makeFluentSet(name, variableDecl.vartype, setType == 0 ? classType : null));
            }
            if (get && !ignore[0].contains(name.toString())) {
                methods.append(makeFluentGet(name, variableDecl.vartype));
            }
        }
        jcClassDecl.defs = jcClassDecl.defs.prependList(methods.toList());
    }

    private JCTree.JCMethodDecl makeFluentGet(Name fieldName, JCTree.JCExpression fieldType) {
        JCTree.JCBlock block =  treeMaker.Block(0L, List.of(
                treeMaker.Return(JCTreeUtils.makeSelect(StrConstant.THIS, fieldName, treeMaker, names))
        ));

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                fieldName,
                fieldType,
                List.nil(),
                List.nil(),
                List.nil(),
                block,
                null);
    }

    public JCTree.JCMethodDecl makeFluentSet(Name fieldName, JCTree.JCExpression fieldType, JCTree.JCExpression returnType) {

        List<JCTree.JCStatement> statements = List.of(JCTreeUtils.makeAssignment(
                treeMaker,
                JCTreeUtils.makeSelect(StrConstant.THIS, fieldName, treeMaker, names),
                treeMaker.Ident(fieldName)
        ));
        if (returnType != null) {
            statements = statements.append(treeMaker.Return(treeMaker.Ident(names.fromString(StrConstant.THIS))));
        }
        JCTree.JCBlock block = treeMaker.Block(0L, statements);

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
                returnType == null ? treeMaker.Type(new Type.JCVoidType()) : returnType,
                List.nil(),
                params,
                List.nil(),
                block,
                null);
    }
}
