package com.xiao.framework.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate annotated target is thread safe.
 * <p>
 *     Only work as a flag.
 *
 * @author lix wang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
    String message() default "";
}
