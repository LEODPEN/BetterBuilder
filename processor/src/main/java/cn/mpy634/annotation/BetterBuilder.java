package cn.mpy634.annotation;

import cn.mpy634.enums.BuilderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BetterBuilder{repo @link https://github.com/LEODPEN/betterBuilder}
 * An annotation to generate better builder codes with fluent
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
 * }</pre>
 * Then we can code in a fluent style --
 * <pre>{@code
 *      Stu stu = Stu.builder().ID(xx).name(xx)....build().ID(xx).name(xx)...
 *  }</pre>
 * More examples see { @link https://github.com/LEODPEN/BetterBuilder/blob/main/README.md }
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
 *      }</pre>
 *      However, there's no need to do so.
 *
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc {@link BetterBuilder @since 1.0.1} supposed to be placed on class.
 * @since 1.0.1
 *
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BetterBuilder {

    /**
     * Allows {@link BetterBuilder} to generate fluent set methods for all the fields.
     * Want to change the return type @see {@link #setType()}.
     * Want to ignore certain fields @see {@link IgnoreSet}.
     */
    boolean fluentSet() default true;

    /**
     * Allows {@link BetterBuilder} to generate fluent get methods for all the fields.
     * Want to ignore certain fields @see {@link IgnoreGet}
     */
    boolean fluentGet() default true;

    /**
     * case 0: the set methods return this. {chain set}
     * case 1: the set methods return void.
     */
    byte setType() default 0;

    /**
     * The builder type that BetterBuilder will generate.
     * @see cn.mpy634.enums.BuilderType
     * @since 1.0.3
     */
    BuilderType BUILDER_TYPE() default BuilderType.CLASSIC;

    /**
     * Annotation that ignores the given fields of a class when generating set method codes for that class.
     * Used when {@link BetterBuilder} is placed on the class.
     *
     * @since 1.0.2
     * @see BetterBuilder
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface IgnoreSet {
    }

    /**
     * Annotation that ignores the given fields of a class when generating get method codes for that class.
     * Used when {@link BetterBuilder} is placed on the class.
     *
     * @since 1.0.2
     * @see BetterBuilder
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface IgnoreGet {
    }
}
