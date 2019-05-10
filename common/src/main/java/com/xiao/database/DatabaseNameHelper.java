package com.xiao.database;

import com.xiao.config.Constants;

import javax.validation.constraints.NotNull;

/**
 *
 * @author lix wang
 */
public class DatabaseNameHelper {
    public static String computeDataSourceName(@NotNull String databaseName) {
        return databaseName + Constants.KEY_DATA_SOURCE;
    }

    public static String computeSqlSessionFactoryName(@NotNull String databaseName) {
        return databaseName + Constants.KEY_SQL_SESSION_FACTORY;
    }

    public static String computeTransactionManagerName(@NotNull String databaseName) {
        return databaseName + Constants.KEY_TRANSACTION_MANAGER;
    }

    public static String computeSqlSessionTemplateName(@NotNull String databaseName) {
        return databaseName + Constants.KEY_SQL_SESSION_TEMPLATE;
    }

    public static Class<? extends DatabaseService> getDatabaseService(DatabaseEnum databaseEnum) {
        switch (databaseEnum) {
            case MYSQL:
                return MysqlDatabaseService.class;
            default:
                throw new RuntimeException("Not support database type");
        }
    }
}
