package com.xiao.framework.biz.database;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lix wang
 */
public class LixSqlSessionFactoryBeanFactory<T> implements FactoryBean<T>, ApplicationContextAware {
    private final Class<T> clazz;
    private ApplicationContext context;

    @Getter
    @Setter
    private DatabaseEnum database;
    @Getter
    @Setter
    private SqlSessionFactoryParam sqlSessionFactoryParam;

    public LixSqlSessionFactoryBeanFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T getObject() throws Exception {
        DataSource dataSource = (DataSource) context.getBean(sqlSessionFactoryParam.getDataSourceName());
        Class<? extends DatabaseService> databaseService = DatabaseNameHelper.getDatabaseService(
                sqlSessionFactoryParam.getDatabase());
        DatabaseService realDatabaseService = context.getBean(databaseService);
        Set<Resource> resources = extractResources(sqlSessionFactoryParam.getMapperLocations());
        return (T) realDatabaseService.createSqlSessionFactory(dataSource,
                Arrays.copyOf(resources.toArray(), resources.size(), Resource[].class), getCustomizers());
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Extract Resources from target mapperLocations.
     */
    private Set<Resource> extractResources(String[] mapperLocations) throws IOException {
        Assert.notEmpty(mapperLocations, "At least one mapper location must be specified");
        Set<Resource> resources = new HashSet<>();
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        for (String mapperLocation : mapperLocations) {
            Resource[] tarResources = patternResolver.getResources(mapperLocation);
            resources.addAll(Arrays.asList(tarResources));
        }
        return resources;
    }

    private List<LixSqlSessionFactoryBeanCustomizer> getCustomizers() {
        Map<String, TypeHandler> typeHandlerBeanMap = context.getBeansOfType(TypeHandler.class);
        TypeHandler<?>[] typeHandlers = typeHandlerBeanMap.values().toArray(new TypeHandler[0]);
        return ImmutableList.of(getTypeHandlerCustomizer(typeHandlers));
    }
    
    private LixSqlSessionFactoryBeanCustomizer getTypeHandlerCustomizer(TypeHandler<?>[] typeHandlers) {
        return bean -> {
            bean.setTypeHandlers(typeHandlers);
            return null;
        };
    }
}
