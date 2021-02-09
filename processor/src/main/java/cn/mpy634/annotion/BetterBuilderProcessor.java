package cn.mpy634.annotion;

import cn.mpy634.constant.StrConstant;
import cn.mpy634.utils.ElementUtils;
import cn.mpy634.utils.JCTreeUtils;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
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
 *              2: 暂略
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cn.mpy634.annotion.BetterBuilder"})
public class BetterBuilderProcessor extends AbstractProcessor {

    // 编译时插入日志
    private Messager messager;

    // 提供抽象语法树
    private JavacTrees javacTrees;

    private TreeMaker treeMaker;

    private Names names;

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
            boolean makeAllArgsConstructor = !ElementUtils.hasAllArgsConstructor(e, e.getModifiers());
            boolean noBuilder = e.getAnnotation(BetterBuilder.class).noBuilder();
            tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil(); // EMPTY
                    for (JCTree jcTree : jcClassDecl.defs){
                        if (jcTree.getKind().equals(Tree.Kind.VARIABLE)){
                            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) jcTree;
                            jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                        }
                    }

                    if (!noBuilder) {
                        // make sure there's an all args constructor.
                        if (makeAllArgsConstructor) {
                            makeConstructor(jcClassDecl, jcVariableDeclList);
                        }

                        // todo builder opt
                    }

                    // fluent
                    makeFluent(jcClassDecl,
                            jcVariableDeclList,
                            e.getAnnotation(BetterBuilder.class).fluentGet(),
                            e.getAnnotation(BetterBuilder.class).fluentSet());


                    super.visitClassDef(jcClassDecl);
                }
            });

        }
        // 如果返回是true的话，那么javac过程会再次重新从解析与填充符号表处开始进行
        return true;
    }

    private void makeFluent(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCVariableDecl> jcVariableDeclList, boolean get, boolean set) {
        for (JCTree.JCVariableDecl variableDecl : jcVariableDeclList) {
//            messager.printMessage(Diagnostic.Kind.NOTE,variableDecl.getName()+" is being processed to be fluent.");
            jcClassDecl.defs = jcClassDecl.defs.prependList(makeFluentMethodDecl(variableDecl, get, set));
//            messager.printMessage(Diagnostic.Kind.NOTE,variableDecl.getName()+" done.");
        }
    }

    private void makeConstructor(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCVariableDecl> variableDecls) {
        jcClassDecl.defs = jcClassDecl.defs.prepend(makeAllArgsConstructor(variableDecls));
    }

    private JCTree.JCMethodDecl makeAllArgsConstructor(List<JCTree.JCVariableDecl> variableDecls) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        ListBuffer<JCTree.JCVariableDecl> params = new ListBuffer<>();
        for (JCTree.JCVariableDecl variable : variableDecls) {
            Name name = variable.getName();
            treeMaker.pos = variable.pos;
            statements.append(JCTreeUtils.makeAssignment(
                    treeMaker,
                    makeSelect(StrConstant.THIS, name),
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

    private List<JCTree> makeFluentMethodDecl(JCTree.JCVariableDecl variableDecl, boolean get, boolean set) {
        Name name = variableDecl.getName();
        ListBuffer<JCTree> methods = new ListBuffer<>();
        treeMaker.pos = variableDecl.pos;
        if (set) {
            List<JCTree.JCStatement> statements = List.of(JCTreeUtils.makeAssignment(
                    treeMaker,
                    makeSelect(StrConstant.THIS, name),
                    treeMaker.Ident(name)
            ));
            JCTree.JCBlock block =  treeMaker.Block(0L, statements);

            // params
            List<JCTree.JCVariableDecl> params = List.of(
                    treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PARAMETER),
                            name,
                            variableDecl.vartype, null)
            );

            methods.append(treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC),
                    name,
                    treeMaker.Type(new Type.JCVoidType()),
                    List.nil(),
                    params,
                    List.nil(),
                    block,
                    null));
        }

        if (get) {
            JCTree.JCBlock block =  treeMaker.Block(0L, List.of(
                    treeMaker.Return(makeSelect(StrConstant.THIS, name))
            ));

            methods.append(treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC),
                    name,
                    variableDecl.vartype,
                    List.nil(),
                    List.nil(),
                    List.nil(),
                    block,
                    null));
        }
        return methods.toList();
    }

    private JCTree.JCExpression makeSelect(String l, Name r) {
        return treeMaker.Select(treeMaker.Ident(names.fromString(l)), r);
    }

}
