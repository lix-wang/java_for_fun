package com.xiao.framework.biz.resolver;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for selected value for request param.
 *
 * @author lix wang
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectedParam {
    String name();

    String defaultValue() default ValueConstants.DEFAULT_NONE;

    boolean required() default true;

    String[] expectedValue() default {};
}
