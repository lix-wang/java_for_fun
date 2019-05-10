package com.xiao.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum of database.
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum DatabaseEnum {
    MYSQL(MysqlDatabaseService.class),
    ORACLE(null),
    SQL_SERVER(null);

    private Class<? extends DatabaseService> dataServiceType;
}
