package com.xiao.framework.thread;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate annotated target is thread safe.
 *
 * @author wang lingxiao(lix.wang@alo7.com)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
    String message() default "";
}
