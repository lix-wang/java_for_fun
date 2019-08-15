package com.xiao.framework.thread;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the target is thread not safe.
 * <p>
 *     Only work as a flag.
 *
 * @author lix wang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadNotSafe {
    String message() default "";
}
