package cn.mpy634.annotion;

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
 * @desc https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("cn.mpy634.annotion.BetterBuilder")
public class BetterBuilderProcessor extends AbstractProcessor {


    // 编译时插入日志
    private Messager messager;

    // 提供抽象语法树
    private JavacTrees javacTrees;

    // 使用 TreeMaker 对象和 Names 来处理 AST

    // 分封了创建AST节点的一些方法
    private TreeMaker treeMaker;

    // 提供创建标识符的方法
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


    // 如果返回是true的话，那么javac过程会再次重新从解析与填充符号表处开始进行
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Represents a program element such as a package, class, or method.
        Set<? extends Element> elementsWithAnnotation = roundEnv.getElementsAnnotatedWith(BetterBuilder.class);
        messager.printMessage(Diagnostic.Kind.NOTE,"the set size is " + elementsWithAnnotation.size());
        for (Element e : elementsWithAnnotation) {
            JCTree tree = javacTrees.getTree(e);
            String className = e.toString();

            // todo builder操作

            makeFluent(tree, className,
                    e.getAnnotation(BetterBuilder.class).fluentGet(),
                    e.getAnnotation(BetterBuilder.class).fluentSet());

        }
        return true;
    }


    private void makeFluent(JCTree tree, String className, boolean get, boolean set) {
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

                jcVariableDeclList.forEach(jcVariableDecl -> {
                    messager.printMessage(Diagnostic.Kind.NOTE,jcVariableDecl.getName()+" is being processed to be fluent.");
                    jcClassDecl.defs = jcClassDecl.defs.prependList(makeFluentMethodDecl(jcVariableDecl, names.fromString(className), get, set));
                    messager.printMessage(Diagnostic.Kind.NOTE,jcVariableDecl.getName()+" done.");
                });
                super.visitClassDef(jcClassDecl);
            }
        });
    }

    private List<JCTree> makeFluentMethodDecl(JCTree.JCVariableDecl variableDecl, Name className, boolean get, boolean set) {
        Name name = variableDecl.getName();
        ListBuffer<JCTree> methods = new ListBuffer<>();

        if (set) {
            List<JCTree.JCStatement> statements = List.of(makeAssignment(
                    // selected：before . | selector：behind .
                    treeMaker.Select(treeMaker.Ident(names.fromString("this")), name),
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
                    treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), name))
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


    // 给变量赋值语句
    private JCTree.JCExpressionStatement makeAssignment(JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
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
