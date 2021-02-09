package cn.mpy634.utils;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;

/**
 * @author LEO D PEN
 * @date 2021/2/9
 * @desc
 */
public class JCTreeUtils {

    // 给变量赋值语句
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



}
