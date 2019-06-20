package com.xiao.framework.biz.database;


import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author lix wang
 */
public class LixSqlSessionTemplateBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {
    private final Class<T> clazz;
    private ApplicationContext context;

    @Getter
    @Setter
    private SqlSessionTemplateParam sqlSessionTemplateParam;

    public LixSqlSessionTemplateBeanFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T getObject() throws Exception {
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) context.getBean(
                sqlSessionTemplateParam.getSqlSessionFactoryName());
        return (T) new SqlSessionTemplate(sqlSessionFactory);
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
