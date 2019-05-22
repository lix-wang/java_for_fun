package com.xiao.environment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author lix wang
 */
@Repeatable(value = EnvConfigs.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvConfig {
    /**
     * environments
     */
    ProfileType[] environments();

    /**
     * value
     */
    String value();

    /**
     * encrypted flag
     */
    boolean encrypted() default false;
}
