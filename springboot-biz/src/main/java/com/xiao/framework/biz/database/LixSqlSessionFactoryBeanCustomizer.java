package com.xiao.framework.biz.database;

import org.mybatis.spring.SqlSessionFactoryBean;

/**
 * Customizer.
 *
 * @author lix wang
 */
@FunctionalInterface
public interface LixSqlSessionFactoryBeanCustomizer<T extends SqlSessionFactoryBean> {
    Object customize(T target);
}
