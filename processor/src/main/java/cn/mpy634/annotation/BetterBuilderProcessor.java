package cn.mpy634.annotation;

import cn.mpy634.constant.StrConstant;
import cn.mpy634.enums.BuilderType;
import cn.mpy634.factory.BuilderFactory;
import cn.mpy634.factory.ClassicBuilderFactory;
import cn.mpy634.factory.NoBuilderFactory;
import cn.mpy634.factory.TypeSafeBuilderFactory;
import cn.mpy634.utils.ElementUtils;
import cn.mpy634.utils.JCTreeUtils;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;


/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc 可能问题1: https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found
 *              2: ...
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cn.mpy634.annotation.BetterBuilder"})
public class BetterBuilderProcessor extends AbstractProcessor {

    // 编译时插入日志
    private Messager messager;

    // 提供抽象语法树
    private JavacTrees javacTrees;

    private TreeMaker treeMaker;

    private Names names;

    private BuilderFactory builderFactory;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Represents a program element such as a package, class, or method.
        Set<? extends Element> elementsWithAnnotation = roundEnv.getElementsAnnotatedWith(BetterBuilder.class);
        messager.printMessage(Diagnostic.Kind.NOTE,"the betterBuilder set size is " + elementsWithAnnotation.size());
        for (Element e : elementsWithAnnotation) {
            if (!ElementUtils.isClass(e)) {
                messager.printMessage(Diagnostic.Kind.ERROR, StrConstant.onlyClassPrefix + BetterBuilder.class.getSimpleName());
                throw new UnsupportedOperationException(StrConstant.onlyClassPrefix + BetterBuilder.class.getSimpleName() + "!");
            }
            JCTree tree = javacTrees.getTree(e);
            BetterBuilder bb = e.getAnnotation(BetterBuilder.class);
            boolean makeAllArgsConstructor = !ElementUtils.hasAllArgsConstructor(e, e.getModifiers());
            Set<String>[] fieldIgnore = ElementUtils.getIgnoreFields(e);
            builderFactory = whichFactory(bb.BUILDER_TYPE());
            // todo 找到@required 结合factory 和 ElementUtils
            builderFactory.dealRequiredFields(e);
            tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    List<JCTree.JCVariableDecl> variableDeclList = JCTreeUtils.getAllVariables(jcClassDecl);
                    builderFactory.setJcClassDecl(jcClassDecl);
                    builderFactory.setVariableDecls(variableDeclList);

                    builderFactory.completeBetterBuilder(makeAllArgsConstructor, fieldIgnore, bb.fluentGet(), bb.fluentSet(), bb.setType());

//                    super.visitClassDef(jcClassDecl); // stackOverFlow
                }
            });

        }
        // true -> javac过程再次重新从解析与填充符号表处开始进行
        return true;
    }

    private BuilderFactory whichFactory(BuilderType type) {
        switch (type) {
            case CLASSIC:
                return new ClassicBuilderFactory(treeMaker, names);
            case NO_BUILDER:
                return new NoBuilderFactory(treeMaker, names);
            case TYPE_SAFE:
                return new TypeSafeBuilderFactory(treeMaker, names);
            default:
                throw new IllegalArgumentException("invalid type");
        }
    }
}
