package com.xiao.framework.biz.database;

import com.xiao.framework.biz.utils.ObjectHelper;
import com.xiao.framework.biz.environment.EnvConfigPostProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * This registrar is used to config database and scanner mybatis mapper interfaces.
 *
 * @author lix wang
 */
@Log4j2
public class LixMapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    /**
     * 本质上是在BeanDefinition加载时，向BeanDefinitionRegistry中注册BeanDefinition，此时，Bean还未初始化。
     * 向BeanDefinition中设置BeanClass，该BeanClass 需要实现FactoryBean。
     * 在获取Bean时，spring会利用反射根据BeanClass找到这些实现了FactoryBean接口的class，并调用getObject方法来获取Bean。
     * {@link EnvConfigPostProcessor} 中就是采用这个方式，在BeanDefinition加载过程中设置BeanClass，
     * 并根据自定义的BeanClass获取Bean。这样获取到的Bean在某种程度上讲，就是我们自定义的Bean。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Class clazz = ObjectHelper.getClassByClassName(importingClassMetadata.getClassName());
        // If the target clazz is not annotated by LixDatabase.class, we don't do anything.
        if (!clazz.isAnnotationPresent(LixDatabase.class) || !BaseLixDatabaseConfig.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Database configuration bean must annotated by "
                    + LixDatabase.class.getName()
                    + "and extends from "
                    + BaseLixDatabaseConfig.class.getName());
        }
        LixDatabase database = (LixDatabase) clazz.getAnnotation(LixDatabase.class);
        // Register database related
        try {
            registerDatabase(database, registry, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Register database failed.", e);
        }
        // According to mapper packages, scan mapper interfaces and register them.
        doScan(database.mapperPackages(), registry,
                DatabaseNameHelper.computeSqlSessionTemplateName(database.databaseName()));
        log.info("Register database bean: " + clazz.getSimpleName());
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Register DataSource SqlSessionFactory TransactionManager SqlSessionTemplate.
     */
    private void registerDatabase(@NotNull LixDatabase databaseAnnotation, BeanDefinitionRegistry registry,
            @NotNull Class<? extends BaseLixDatabaseConfig> targetClass) throws IOException {
        // Construct dataSource BeanDefinition.
        String dataSourceName = DatabaseNameHelper.computeDataSourceName(databaseAnnotation.databaseName());
        DataSourceParam dataSourceParam = DataSourceParam.builder()
                .database(databaseAnnotation.database()).build();
        BeanDefinition dataSourceBeanDefinition = getDataSourceBeanDefinition(LixDataSourceBeanFactory.class,
                dataSourceParam, targetClass);
        // Construct sqlSessionFactory BeanDefinition.
        String sqlSessionFactoryName = DatabaseNameHelper.computeSqlSessionFactoryName(
                databaseAnnotation.databaseName());
        SqlSessionFactoryParam sqlSessionFactoryParam = SqlSessionFactoryParam.builder()
                .dataSourceName(dataSourceName)
                .database(databaseAnnotation.database())
                .mapperLocations(databaseAnnotation.mapperLocations()).build();
        BeanDefinition sqlSessionFactoryBeanDefinition = getSqlSessionFactoryBeanDefinition(
                LixSqlSessionFactoryBeanFactory.class, sqlSessionFactoryParam);
        // Construct transactionManager BeanDefinition.
        String transactionManagerName = DatabaseNameHelper.computeTransactionManagerName(
                databaseAnnotation.databaseName());
        TransactionManagerParam transactionManagerParam = TransactionManagerParam.builder()
                .dataSourceName(dataSourceName)
                .build();
        BeanDefinition transactionManagerBeanDefinition = getTransactionManagerBeanDefinition(
                LixTransactionManagerBeanFactory.class, transactionManagerParam);
        // Construct sqlSessionTemplate BeanDefinition.
        String sqlSessionTemplateName = DatabaseNameHelper.computeSqlSessionTemplateName(
                databaseAnnotation.databaseName());
        SqlSessionTemplateParam sqlSessionTemplateParam = SqlSessionTemplateParam.builder()
                .sqlSessionFactoryName(sqlSessionFactoryName)
                .build();
        BeanDefinition sqlSessionTemplateBeanDefinition = getSqlSessionTemplateBeanDefinition(
                LixSqlSessionTemplateBeanFactory.class, sqlSessionTemplateParam);

        // Register BeanDefinitions
        registry.registerBeanDefinition(dataSourceName, dataSourceBeanDefinition);
        registry.registerBeanDefinition(sqlSessionFactoryName, sqlSessionFactoryBeanDefinition);
        registry.registerBeanDefinition(transactionManagerName, transactionManagerBeanDefinition);
        registry.registerBeanDefinition(sqlSessionTemplateName, sqlSessionTemplateBeanDefinition);
    }

    /**
     * Get general dataSource BeanDefinition.
     */
    private BeanDefinition getDataSourceBeanDefinition(@NotNull Class<? extends FactoryBean> dataSourceBeanFactory,
            @NotNull DataSourceParam databaseParam, @NotNull Class<? extends BaseLixDatabaseConfig> targetClass) {
        BeanDefinition dataSourceBeanDefinition = generateBeanDefinition(DataSource.class.getName(),
                dataSourceBeanFactory);
        dataSourceBeanDefinition.getPropertyValues().add("dataSourceParam", databaseParam);
        dataSourceBeanDefinition.getPropertyValues().add("targetClass", targetClass);
        return dataSourceBeanDefinition;
    }

    /**
     * Get general sqlSessionFactory BeanDefinition.
     */
    private BeanDefinition getSqlSessionFactoryBeanDefinition(
            @NotNull Class<? extends FactoryBean> sqlSessionFactoryBeanFactory, @NotNull SqlSessionFactoryParam param) {
        BeanDefinition sqlSessionFactoryBeanDefinition = generateBeanDefinition(SqlSessionFactory.class.getName(),
                sqlSessionFactoryBeanFactory);
        sqlSessionFactoryBeanDefinition.getPropertyValues().add("sqlSessionFactoryParam", param);
        return sqlSessionFactoryBeanDefinition;
    }

    /**
     * Get general transactionManager BeanDefinition.
     */
    private BeanDefinition getTransactionManagerBeanDefinition(
            @NotNull Class<? extends FactoryBean> transactionManagerFactory, @NotNull TransactionManagerParam param) {
        BeanDefinition transactionManagerBeanDefinition = generateBeanDefinition(
                DataSourceTransactionManager.class.getName(), transactionManagerFactory);
        transactionManagerBeanDefinition.getPropertyValues().add("transactionManagerParam", param);
        return transactionManagerBeanDefinition;
    }

    private BeanDefinition getSqlSessionTemplateBeanDefinition(
            @NotNull Class<? extends FactoryBean> sqlSessionTemplateFactory, @NotNull SqlSessionTemplateParam param) {
        BeanDefinition sqlSessionTemplateBeanDefinition = generateBeanDefinition(SqlSessionTemplate.class.getName(),
                sqlSessionTemplateFactory);
        sqlSessionTemplateBeanDefinition.getPropertyValues().add("sqlSessionTemplateParam", param);
        return sqlSessionTemplateBeanDefinition;
    }

    /**
     * Generate general BeanDefinition.
     */
    private BeanDefinition generateBeanDefinition(@NotNull String className, @NotNull Class<?> targetBeanFactory) {
        GenericBeanDefinition newBeanDefinition = new GenericBeanDefinition();
        newBeanDefinition.setBeanClass(targetBeanFactory);
        newBeanDefinition.setPrimary(true);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(className);
        newBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
        return newBeanDefinition;
    }

    /**
     * Scan mapperPackages register target mapper interfaces as beanDefinitions.
     */
    private Set<BeanDefinitionHolder> doScan(String[] mapperPackages, @NotNull BeanDefinitionRegistry registry,
            @NotNull String sqlSessionTemplateName) {
        Assert.notEmpty(mapperPackages, "At least one mapper packages must be specified");
        LixMapperScanner scanner = getScanner(registry);
        scanner.setSqlSessionTemplateName(sqlSessionTemplateName);
        return scanner.doMapperScan(mapperPackages);
    }

    /**
     * Define a Scanner to scan target interfaces, now, we let the scanner scan all interfaces.
     */
    private LixMapperScanner getScanner(@NotNull BeanDefinitionRegistry registry) {
        LixMapperScanner scanner = new LixMapperScanner(registry);
        // If you don't set resourceLoader scanner will scan out strange beanDefinitions.
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        return scanner;
    }
}
