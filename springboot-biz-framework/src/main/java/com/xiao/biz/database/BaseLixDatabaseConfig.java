package com.xiao.biz.database;

import lombok.Getter;

/**
 *
 * @author lix wang
 */
@Getter
public abstract class BaseLixDatabaseConfig {
    private final String databaseUrl;
    private final String databaseUserName;
    private final String databasePassword;

    protected BaseLixDatabaseConfig(String databaseUrl, String databaseUserName, String databasePassword) {
        this.databaseUrl = databaseUrl;
        this.databaseUserName = databaseUserName;
        this.databasePassword = databasePassword;
    }
}
