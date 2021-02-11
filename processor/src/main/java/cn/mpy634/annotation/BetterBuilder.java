package cn.mpy634.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BetterBuilder{repo @link https://github.com/LEODPEN/betterBuilder}
 * An annotation to generate better builder codes with <strong>fluent<strong/>
 * get/set methods, which can make coding much more comfortable.
 *
 * 0. Usage
 * One most simple example could be --
 * <pre>{@code
 *      @BetterBuilder
 *      public class Stu {
 *          private String name;
 *          private Integer ID;
 *      }
 * Then we can code in a fluent style --
 * <pre>{@code
 *      Stu stu = Stu.builder().ID(xx).name(xx)....build().ID(xx).name(xx)...
 *  }</pre>
 *
 * 1. Some extra instructions
 *   1.0 Pos
 *      Only classes can be annotated with BetterBuilder;
 *      Only fields can be annotated with annotations of ignore operations;
 *   1.1 Constructor
 *      BetterBuilder will not delete other constructors.
 *      If there's no all arguments constructor, BetterBuilder will generate one. [It's the same as lombok]
 *   1.2 With lombok
 *      It's Ok to use both BetterBuilder and lombok{repo @link https://github.com/rzwitserloot/lombok}
 *      annotations, for example:
 *      <pre>{@code
 *      @BetterBuilder(fluentGet = false, fluentSet = false)
 *      @Data
 *      public class Stu {
 *      ...}
 *      However, there's no need to do so.
 * }</pre>
 *
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc @BetterBuilder 1.0.1
 *
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BetterBuilder {

    boolean fluentSet() default true;

    boolean fluentGet() default true;

    /**
     * case 0: the set methods return this. {chain set}
     * case 1: the set methods return void.
     */
    byte setType() default 0;

    /**
     * If needs fluentGet or fluentSet operations only, just make noBuilder = true
     */
    boolean noBuilder() default false;
}
