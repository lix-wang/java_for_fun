package com.xiao.database;

import com.xiao.utils.JodaUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * This helper is to generate datasource and sqlSessionFactory.
 *
 * @author lix wang
 */
@Service
public class MysqlDatabaseService implements DatabaseService {
    private static final int DEFAULT_MAX_POOL_SIZE = 32;
    private static final int MIN_IDEL = 1;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 5 * JodaUtils.MILLONS_PER_SECOND;
    private static final long IDEL_TIMEOUT = JodaUtils.SENCONDS_PER_MINUTE * JodaUtils.MILLONS_PER_SECOND;

    @Override
    public DataSource createDataSource(@NotNull String database, @NotNull String databaseUserName,
            @NotNull String databasePassword) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(database);
        config.setUsername(databaseUserName);
        config.setPassword(databasePassword);
        // Max pool size include occupied and free ones, default 10.
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        // Min connection size of the pool.
        config.setMinimumIdle(MIN_IDEL);
        // Timeout of waiting connection distribution(ms). If timeout, will throw out SQLException.
        config.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        // Idle connection state timeout(ms), the idle will release after timeout, default 10 mins.
        config.setIdleTimeout(IDEL_TIMEOUT);
        // 如果为正，表示初始连接超时时间（毫秒），如果为0，连接获取验证不过时，抛出异常池不启动,
        // 如果无法获取连接，则启动连接池，后续再获取连接，后续获取连接的操作可能失败
        config.setInitializationFailTimeout(0);
        // 是否自定义配置，为true下面两个才生效
        config.addDataSourceProperty("cachePrepStmts", true);
        // 连接池大小 默认25 官方推荐 250-500
        config.addDataSourceProperty("prepStmtCacheSize", "10");
        // 单条词句最大长度 默认256 官方推荐2048
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    @Override
    public SqlSessionFactory createSqlSessionFactory(@NotNull DataSource dataSource,
            @NotNull Resource[] resources) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(resources);
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        return bean.getObject();
    }
}
