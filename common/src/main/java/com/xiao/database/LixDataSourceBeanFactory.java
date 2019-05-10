package com.xiao.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * DataSource Bean factory.
 *
 * @author lix wang
 */
public class LixDataSourceBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {
    private final Class<T> clazz;
    private ApplicationContext context;
    @Getter
    @Setter
    private DataSourceParam dataSourceParam;
    @Getter
    @Setter
    private Class<? extends BaseLixDatabaseConfig> targetClass;

    public LixDataSourceBeanFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T getObject() throws Exception {
        Class<? extends DatabaseService> targetService = DatabaseNameHelper.getDatabaseService(
                dataSourceParam.getDatabase());
        DatabaseService databaseService = context.getBean(targetService);
        BaseLixDatabaseConfig databaseConfig = context.getBean(targetClass);
        return (T) databaseService.createDataSource(databaseConfig.getDatabaseUrl(),
                databaseConfig.getDatabaseUserName(), databaseConfig.getDatabasePassword());
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
