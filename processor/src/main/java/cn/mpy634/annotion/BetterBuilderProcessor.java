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
        messager.printMessage(Diagnostic.Kind.NOTE,"the set size is " + String.valueOf(elementsWithAnnotation.size()));
        for (Element e : elementsWithAnnotation) {
            JCTree tree = javacTrees.getTree(e);
            String className = e.toString();

            // todo builder操作

            if (e.getAnnotation(BetterBuilder.class).fluent()) {
                makeFluent(tree, className);
            }
        }
        return true;
    }


    private void makeFluent(JCTree tree, String className) {
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
                    jcClassDecl.defs = jcClassDecl.defs.prepend(makeFluentMethodDecl(jcVariableDecl, names.fromString(className)));
                    messager.printMessage(Diagnostic.Kind.NOTE,jcVariableDecl.getName()+" done.");
                });
                super.visitClassDef(jcClassDecl);
            }
        });
    }

    private JCTree.JCMethodDecl makeFluentMethodDecl(JCTree.JCVariableDecl variableDecl, Name className) {
        Name name = variableDecl.getName();
        // body 语句块
        List<JCTree.JCStatement> statements = List.of(makeAssignment(
                // selected：before . | selector：behind .
                treeMaker.Select(treeMaker.Ident(names.fromString("this")), name),
                treeMaker.Ident(name)
        ));
// treeMaker.Return(treeMaker.Ident(names.fromString("null")))
        JCTree.JCBlock block =  treeMaker.Block(0L, statements);

        // params fluent直接一个参数就OK
        List<JCTree.JCVariableDecl> params = List.of(
                treeMaker.VarDef(
                        treeMaker.Modifiers(Flags.PARAMETER),
                        name,
                        variableDecl.vartype, null)
        );

        // return statement

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                name,
//                treeMaker.Ident(className),
                treeMaker.Type(new Type.JCVoidType()),
                List.nil(),
                params,
                List.nil(),
                block,
                null);
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
