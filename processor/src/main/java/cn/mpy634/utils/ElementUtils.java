package cn.mpy634.utils;

import javax.lang.model.element.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

}
