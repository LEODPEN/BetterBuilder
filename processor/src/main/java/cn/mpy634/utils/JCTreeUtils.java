package cn.mpy634.utils;

import cn.mpy634.constant.StrConstant;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

/**
 * @author LEO D PEN
 * @date 2021/2/9
 * @desc utils - JCTree
 */
public class JCTreeUtils {

    public static JCTree.JCExpressionStatement makeAssignment(TreeMaker treeMaker , JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
        // 创建可执行语句语法树节点
        return treeMaker.Exec(
                // assignment
                treeMaker.Assign(
                        lhs,
                        rhs
                )
        );
    }

    public static JCTree.JCExpression makeSelect(String l, Name r, TreeMaker treeMaker, Names names) {
        return treeMaker.Select(treeMaker.Ident(names.fromString(l)), r);
    }

    public static List<JCTree.JCVariableDecl> getAllVariables(JCTree.JCClassDecl jcClassDecl) {
        ListBuffer<JCTree.JCVariableDecl> variableDecls = new ListBuffer<>();
        for (JCTree jcTree : jcClassDecl.defs){
            if (jcTree.getKind().equals(Tree.Kind.VARIABLE)){
                variableDecls.append((JCTree.JCVariableDecl) jcTree);
//                Set<Modifier> flagSets = jcVariableDecl.mods.getFlags();
//                if (!flagSets.contains(Modifier.STATIC)) {
//                    variableDecls.append(jcVariableDecl);
//                }
            }
        }
        return variableDecls.toList();
    }

    public static JCTree.JCMethodDecl makeAllArgsConstructor(List<JCTree.JCVariableDecl> variableDecls, TreeMaker treeMaker, Names names) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        ListBuffer<JCTree.JCVariableDecl> params = new ListBuffer<>();
        for (JCTree.JCVariableDecl variable : variableDecls) {
            Name name = variable.getName();
            treeMaker.pos = variable.pos;
            statements.append(JCTreeUtils.makeAssignment(
                    treeMaker,
                    makeSelect(StrConstant.THIS, name, treeMaker, names),
                    treeMaker.Ident(name)
            ));
            params.append(treeMaker.VarDef(
                    treeMaker.Modifiers(Flags.PARAMETER),
                    name,
                    variable.vartype, null));
        }
        JCTree.JCBlock block = treeMaker.Block(0L, statements.toList());

        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("<init>"),
                treeMaker.Type(null),
                List.nil(),
                params.toList(),
                List.nil(),
                block,
                null);
    }

    public static JCTree.JCMethodDecl makeNoArgsConstructor(TreeMaker treeMaker, Names names, boolean isPublic) {
        return treeMaker.MethodDef(
                treeMaker.Modifiers(isPublic ? Flags.PUBLIC : Flags.PRIVATE),
                names.fromString("<init>"),
                treeMaker.Type(null),
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0L, List.nil()),
                null);
    }

    public static List<JCTree.JCVariableDecl> copyFields(List<JCTree.JCVariableDecl> sourceFields, TreeMaker treeMaker, Names names) {
        ListBuffer<JCTree.JCVariableDecl> targetFields = new ListBuffer<>();
        sourceFields.stream()
                .map(e -> {
                    JCTree.JCVariableDecl v = treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PRIVATE),
                            names.fromString(e.name.toString()),
                            e.vartype,
                            null
                    );
                    v.pos = e.pos;
                    return v;
                }).forEach(targetFields::append);
        return targetFields.toList();
    }





}
