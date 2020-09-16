package com.xiao.framework.biz.database;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to config database
 *
 * @author lix wang
 */
@Retention(RetentionPolicy.RUNTIME)
@Import(LixMapperScannerRegistrar.class)
@Target(ElementType.TYPE)
public @interface LixDatabase {
    String databaseName();

    String[] mapperPackages();

    String[] mapperLocations() default {};

    DatabaseEnum database() default DatabaseEnum.MYSQL;
}
