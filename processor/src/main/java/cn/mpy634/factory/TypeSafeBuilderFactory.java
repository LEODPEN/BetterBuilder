package cn.mpy634.factory;

import cn.mpy634.utils.ElementUtils;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author LEO D PEN
 * @date 2021/2/14
 * @desc
 */
public class TypeSafeBuilderFactory extends BuilderFactory{

    private List<String> builderRequiredList;

    public TypeSafeBuilderFactory(TreeMaker treeMaker, Names names) {
        super(treeMaker, names);
    }

    @Override
    public void dealRequiredFields(Element e) {
        builderRequiredList = ElementUtils.constructTypeSaleList(e);
    }

    @Override
    public void makeBuilder() {
        // todo
    }

    @Override
    public void makeBuilderMethod(JCTree.JCClassDecl innerClass) {
        // todo
    }
}
