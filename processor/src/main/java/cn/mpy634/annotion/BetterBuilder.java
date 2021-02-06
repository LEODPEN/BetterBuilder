package cn.mpy634.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BetterBuilder {

    boolean fluentSet() default false;

    boolean fluentGet() default false;

}
