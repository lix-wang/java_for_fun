package com.xiao.config;

import com.xiao.framework.biz.database.MysqlDatabaseService;
import com.xiao.framework.biz.constant.Constants;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 原始配置多数据源的方法。
 * Config common datasource.
 *
 * @author lix wang
 */
// @Component
// @MapperScan(basePackages = "com.xiao.mapper.common", sqlSessionTemplateRef = "commonSqlSessionTemplate")
public class CommonDatabaseConfig {
    private static final String MAPPER_XML_PATH = "classpath:com/xiao/mapper/common/*.xml";
    private static final String KEY_DATABASE_ANME = "common";


    private final CommonConfig commonConfig;
    private final MysqlDatabaseService mysqlDatabaseService;

    @Autowired
    public CommonDatabaseConfig(CommonConfig commonConfig, MysqlDatabaseService mysqlDatabaseService) {
        this.commonConfig = commonConfig;
        this.mysqlDatabaseService = mysqlDatabaseService;
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
    @Primary
    public DataSource getDataSource() {
        return mysqlDatabaseService.createDataSource(commonConfig.getCommonDatabase(),
                commonConfig.getCommonDatabaseUserName(), commonConfig.getCommonDatabasePassword());
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_FACTORY)
    @Primary
    public SqlSessionFactory getSqlSessionFactory(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
            DataSource dataSource) throws Exception {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        return mysqlDatabaseService.createSqlSessionFactory(dataSource, patternResolver.getResources(MAPPER_XML_PATH));
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_TRANSACTION_MANAGER)
    @Primary
    public DataSourceTransactionManager getTransactionManager(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
            DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_TEMPLATE)
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_FACTORY)
            SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
