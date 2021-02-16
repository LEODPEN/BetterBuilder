package cn.mpy634.utils;

import cn.mpy634.annotation.BetterBuilder;
import cn.mpy634.constant.StrConstant;

import javax.lang.model.element.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LEO D PEN
 * @date 2021/2/8
 * @desc utils - elms
 */
public class ElementUtils {

    public static String[] getClassFullPathNameArrSplitByDot(Element element) {
        return element.toString().split("\\.");
    }

    public static boolean isClass(Element element) {
        return element.getKind() == ElementKind.CLASS;
    }

    public static boolean hasAllArgsConstructor(Element classElement, Set<Modifier> modifiers) {
        if (!isClass(classElement)) {
            return false;
        }
        int fieldNum = getFields(classElement).size();
        if (modifiers == null) {
            modifiers = Collections.emptySet();
        }
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == fieldNum) {
                    // Found an all args constructor
                    return modifiers.isEmpty() || constructorElement.getModifiers().containsAll(modifiers);
                }
            }
        }
        return false;
    }

    public static List<VariableElement> getFields(Element classElement) {
        if (!isClass(classElement)) {
            return Collections.emptyList();
        }
        return classElement.getEnclosedElements().stream()
                .filter(elm -> elm.getKind().equals(ElementKind.FIELD))
                .map(elm -> (VariableElement) elm)
                .collect(Collectors.toList());
    }

    public static Set<String>[] getIgnoreFields(Element classElement) {
        List<VariableElement> fields = getFields(classElement);
        Set<String>[] ignore = new Set[2];
        Set<String> ignoreGet = new HashSet<>();
        Set<String> ignoreSet = new HashSet<>();
        ignore[0] = ignoreGet; ignore[1] = ignoreSet;
        for (VariableElement vlm : fields) {
            if (!vlm.getModifiers().contains(Modifier.STATIC)) {
                BetterBuilder.IgnoreGet ig = vlm.getAnnotation(BetterBuilder.IgnoreGet.class);
                BetterBuilder.IgnoreSet is = vlm.getAnnotation(BetterBuilder.IgnoreSet.class);
                String name = vlm.getSimpleName().toString();
                if (ig != null) ignoreGet.add(name);
                if (is != null) ignoreSet.add(name);
            }
        }
        return ignore;
    }

    public static Map<String, String> constructTypeSaleList(Element e) {
        List<VariableElement> fields = getFields(e);
        Set<String> fnames = fields.stream().map(v -> v.getSimpleName().toString()).collect(Collectors.toSet());
        Map<String, String> map = new HashMap<>();
        for (VariableElement vlm : fields) {
            if (!vlm.getModifiers().contains(Modifier.STATIC)) {
                BetterBuilder.Required ig = vlm.getAnnotation(BetterBuilder.Required.class);
                String name = vlm.getSimpleName().toString();
                if (ig != null) {
                    String toGenerate = StrConstant.requirePrefix + name;
                    while (fnames.contains(toGenerate)) {
                        toGenerate += Objects.hashCode(toGenerate);
                    }
                    fnames.add(toGenerate);
                    map.put(name, toGenerate);
                }
            }
        }
        return map;
    }

}
