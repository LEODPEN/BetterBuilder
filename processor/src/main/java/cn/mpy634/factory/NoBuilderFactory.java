package cn.mpy634.factory;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;

/**
 * @author LEO D PEN
 * @date 2021/2/14
 * @desc
 */
public class NoBuilderFactory extends BuilderFactory {


    public NoBuilderFactory(TreeMaker treeMaker, Names names) {
        super(treeMaker, names);
    }

    @Override
    public void dealRequiredFields(Element e) {
        // ignore or give bad op msg.
    }

    @Override
    public void makeAgsConstructor(boolean flag) {
        // ignore
    }

    @Override
    public void makeBuilder() {
        // ignore
    }

    @Override
    public void makeBuilderMethod(JCTree.JCClassDecl innerClass) {
        // ignore
    }


}
