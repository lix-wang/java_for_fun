package com.xiao.database;

import com.xiao.config.CommonConfig;
import com.xiao.config.Constants;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Config common datasource.
 *
 * @author lix wang
 */
@Data
@Component
@MapperScan(basePackages = "com.xiao.mapper.common", sqlSessionTemplateRef = "commonSqlSessionTemplate")
public class CommonDataBaseConfig {
    private static final String MAPPER_XML_PATH = "classpath:com/xiao/mapper/common/*.xml";
    private static final String KEY_DATABASE_ANME = "common";


    private final CommonConfig commonConfig;

    @Autowired
    public CommonDataBaseConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
    @Primary
    public DataSource getDataSource() {
        return DatabaseHelper.createDataSource(commonConfig.getCommonDatabase(),
                commonConfig.getCommonDatabaseUserName(), commonConfig.getCommonDatabasePassword());
    }

    @Bean(name = KEY_DATABASE_ANME + Constants.KEY_SQL_SESSION_FACTORY)
    @Primary
    public SqlSessionFactory getSqlSessionFactory(@Qualifier(KEY_DATABASE_ANME + Constants.KEY_DATA_SOURCE)
            DataSource dataSource) throws Exception {
        return DatabaseHelper.createSqlSessionFactory(dataSource, MAPPER_XML_PATH);
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
