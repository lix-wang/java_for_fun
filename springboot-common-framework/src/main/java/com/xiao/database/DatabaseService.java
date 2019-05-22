package com.xiao.database;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Base Service to invoke real DatabaseServiceImpl.
 *
 * @author lix wang
 */
public interface DatabaseService {
    DataSource createDataSource(@NotNull String database, @NotNull String databaseUserName,
            @NotNull String databasePassword);

    SqlSessionFactory createSqlSessionFactory(@NotNull DataSource dataSource, @NotNull Resource[] resources)
            throws Exception;
}
