package com.xiao.framework.biz.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 *
 * @author lix wang
 */
public class LixTransactionManagerBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {
    private final Class<T> clazz;
    private ApplicationContext context;

    @Getter
    @Setter
    private DatabaseEnum database;
    @Getter
    @Setter
    private TransactionManagerParam transactionManagerParam;

    public LixTransactionManagerBeanFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T getObject() throws Exception {
        DataSource dataSource = (DataSource) context.getBean(transactionManagerParam.getDataSourceName());
        return (T) new DataSourceTransactionManager(dataSource);
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
