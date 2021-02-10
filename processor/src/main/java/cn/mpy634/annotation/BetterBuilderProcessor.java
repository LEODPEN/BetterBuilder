package cn.mpy634.annotation;

import cn.mpy634.constant.StrConstant;
import cn.mpy634.utils.ElementUtils;
import cn.mpy634.utils.JCTreeUtils;
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
            boolean noBuilder = bb.noBuilder();
            tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    List<JCTree.JCVariableDecl> variableDeclList = JCTreeUtils.getAllVariables(jcClassDecl);

                    if (!noBuilder) {
                        completeBuilder(jcClassDecl, variableDeclList, makeAllArgsConstructor);
                    }

                    makeFluent(jcClassDecl,
                            variableDeclList,
                            bb.fluentGet(),
                            bb.fluentSet(),
                            bb.setType());
//                    super.visitClassDef(jcClassDecl); // stackOverFlow
                }
            });

        }
        // true -> javac过程再次重新从解析与填充符号表处开始进行
        return true;
    }

    private void completeBuilder(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCVariableDecl> variableDecls, boolean makeAllArgsConstructor) {
        // make sure there's an all args constructor.
        if (makeAllArgsConstructor) {
            makeConstructor(jcClassDecl, variableDecls);
        }
        JCTree.JCClassDecl builderClassDecl = makeBuilder(jcClassDecl, variableDecls);
        makeBuilderMethod(jcClassDecl, builderClassDecl);
    }

    // preparation for @ignore
    private void makeFluent(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCVariableDecl> variableDecls, boolean get, boolean set, byte setType) {
        for (JCTree.JCVariableDecl variableDecl : variableDecls) {
//            messager.printMessage(Diagnostic.Kind.NOTE,variableDecl.getName()+" is being processed to be fluent.");
            jcClassDecl.defs = jcClassDecl.defs.prependList(makeFluentMethodDecl(variableDecl, get, set, setType, treeMaker.Ident(jcClassDecl.name)));
//            messager.printMessage(Diagnostic.Kind.NOTE,variableDecl.getName()+" done.");
        }
    }

    private void makeConstructor(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCVariableDecl> variableDecls) {
        jcClassDecl.defs = jcClassDecl.defs.prepend(makeAllArgsConstructor(variableDecls));
    }

    private void makeBuilderMethod(JCTree.JCClassDecl outerClass, JCTree.JCClassDecl innerClass) {
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
        outerClass.defs = outerClass.defs.prepend(builder);
    }

    private JCTree.JCClassDecl makeBuilder(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCVariableDecl> variableDecls) {
        Name builderClassName = names.fromString(jcClassDecl.name.toString() + StrConstant.builderSuffix);
        List<JCTree.JCVariableDecl> builderFields = copyFields(variableDecls);
        JCTree.JCMethodDecl constructor = makeNoArgsConstructor();

        ListBuffer<JCTree.JCMethodDecl> setters = new ListBuffer<>();
        for (JCTree.JCVariableDecl var : builderFields) {
            // todo @required
            treeMaker.pos = var.pos;
            setters.append(makeFluentSet(var.name, var.vartype, treeMaker.Ident(builderClassName)));
        }
        JCTree.JCMethodDecl buildMethod = makeBuildMethod(jcClassDecl, builderFields);

        JCTree.JCClassDecl builderClass = makeBuilderClass(builderClassName,
                    builderFields,
                    constructor,
                    setters.toList(),
                    buildMethod);
        jcClassDecl.defs = jcClassDecl.defs.append(builderClass);
        return builderClass;
    }

    private JCTree.JCMethodDecl makeBuildMethod(JCTree.JCClassDecl toBuildClass, List<JCTree.JCVariableDecl> variableDecls) {
        JCTree.JCExpression returnType = treeMaker.Ident(toBuildClass.name);
        ListBuffer<JCTree.JCExpression> argsType = new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> args = new ListBuffer<>();
        for (JCTree.JCVariableDecl variableDecl : variableDecls) {
            args.append(makeSelect(StrConstant.THIS, variableDecl.name));
            argsType.append(variableDecl.vartype);
        }
        List<JCTree.JCStatement> statements = List.of(
          treeMaker.Return(
                  treeMaker.NewClass(
                    null,
                    argsType.toList(),
                    returnType,
                    args.toList(),
                    null
          ))
        );
        return treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC),
                    names.fromString(StrConstant.BUILD),
                    returnType,
                    List.nil(),
                    List.nil(),
                    List.nil(),
                    treeMaker.Block(0L, statements),
                    null
                );
    }


    private JCTree.JCClassDecl makeBuilderClass(Name className, List<JCTree.JCVariableDecl> fields,
                                                       JCTree.JCMethodDecl constructor, List<JCTree.JCMethodDecl> setters,
                                                       JCTree.JCMethodDecl buildMethod) {
        ListBuffer<JCTree> classBody = new ListBuffer<>();
        classBody.appendList(List.convert(JCTree.class, fields))
                .append(constructor)
                .appendList(List.convert(JCTree.class, setters))
                .append(buildMethod);
        return treeMaker.ClassDef(
                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                    className,
                    List.nil(),
                    null,
                    List.nil(),
                    classBody.toList()
        );
    }

    private List<JCTree> makeFluentMethodDecl(JCTree.JCVariableDecl variableDecl, boolean get, boolean set, byte setType, JCTree.JCIdent classType) {
        Name name = variableDecl.getName();
        ListBuffer<JCTree> methods = new ListBuffer<>();
        treeMaker.pos = variableDecl.pos;
        if (set) {
            methods.append(makeFluentSet(name, variableDecl.vartype, setType == 0 ? classType : null));
        }

        if (get) {
            methods.append(makeFluentGet(name, variableDecl.vartype));
        }
        return methods.toList();
    }

    private JCTree.JCMethodDecl makeFluentGet(Name fieldName, JCTree.JCExpression fieldType) {
        JCTree.JCBlock block =  treeMaker.Block(0L, List.of(
                treeMaker.Return(makeSelect(StrConstant.THIS, fieldName))
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

    private JCTree.JCMethodDecl makeFluentSet(Name fieldName, JCTree.JCExpression fieldType, JCTree.JCExpression returnType) {

        List<JCTree.JCStatement> statements = List.of(JCTreeUtils.makeAssignment(
                treeMaker,
                makeSelect(StrConstant.THIS, fieldName),
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

    // for builder, must private
    private JCTree.JCMethodDecl makeNoArgsConstructor() {
        return treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PRIVATE),
                    names.fromString("<init>"),
                    treeMaker.Type(null),
                    List.nil(),
                    List.nil(),
                    List.nil(),
                    treeMaker.Block(0L, List.nil()),
                    null);
    }

    private List<JCTree.JCVariableDecl> copyFields(List<JCTree.JCVariableDecl> sourceFields) {
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

    private JCTree.JCExpression makeSelect(String l, Name r) {
        return treeMaker.Select(treeMaker.Ident(names.fromString(l)), r);
    }

}
