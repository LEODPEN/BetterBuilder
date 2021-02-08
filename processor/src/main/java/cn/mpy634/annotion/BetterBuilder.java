package cn.mpy634.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc
 * 0. About Fluent
 *
 * 1. Usage
 *
 * 2. Some extra instructions
 *   2.0 Pos
 *      Only classes can be annotated with BetterBuilder;
 *      Only fields can be annotated with annotations of ignore operations;
 *   2.1 Constructor
 *      BetterBuilder will not delete other constructors.
 *      If there's no all arguments constructor, BetterBuilder will generate one. [It's the same as lombok]
 *
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BetterBuilder {

    boolean fluentSet() default true;

    boolean fluentGet() default true;

    /**
     * If only needs fluentGet or fluentSet operations, just make noBuilder = true
     */
    boolean noBuilder() default false;
}
