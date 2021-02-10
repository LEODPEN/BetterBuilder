package cn.mpy634.utils;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

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



}
