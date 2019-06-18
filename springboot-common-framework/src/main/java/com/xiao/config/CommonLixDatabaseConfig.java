package com.xiao.config;

import com.xiao.biz.database.BaseLixDatabaseConfig;
import com.xiao.biz.database.LixDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lix wang
 */
@Component
@LixDatabase(
        databaseName = CommonLixDatabaseConfig.DATABASE_NAME,
        mapperPackages = "com.xiao.mapper.common",
        mapperLocations = "classpath:com/xiao/mapper/common/*.xml")
public class CommonLixDatabaseConfig extends BaseLixDatabaseConfig {
    public static final String DATABASE_NAME = "common";

    @Autowired
    protected CommonLixDatabaseConfig(CommonConfig commonConfig) {
        super(commonConfig.getCommonDatabase(), commonConfig.getCommonDatabaseUserName(),
                commonConfig.getCommonDatabasePassword());
    }
}
