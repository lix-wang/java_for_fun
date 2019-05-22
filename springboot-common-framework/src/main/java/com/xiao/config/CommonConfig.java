package com.xiao.config;

import com.xiao.environment.EnvConfig;
import com.xiao.environment.LixConfiguration;
import com.xiao.environment.ProfileType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lix wang
 */
@LixConfiguration
public class CommonConfig {
    @EnvConfig(
            environments = {
                    ProfileType.DEV,
                    ProfileType.ALPHA,
                    ProfileType.BETA,
                    ProfileType.PROD},
            value = "jdbc:mysql://localhost:3306/spring_boot_demo")
    @Getter
    @Setter
    private String commonDatabase = "jdbc:mysql://localhost:3306/spring_boot_demo";

    @EnvConfig(
            environments = {
                    ProfileType.DEV,
                    ProfileType.ALPHA,
                    ProfileType.BETA,
                    ProfileType.PROD},
            value = "root")
    @Getter
    @Setter
    private String commonDatabaseUserName = "root";

    @EnvConfig(
            environments = {
                    ProfileType.DEV,
                    ProfileType.ALPHA,
                    ProfileType.BETA,
                    ProfileType.PROD},
            value = "123456")
    @Getter
    @Setter
    private String commonDatabasePassword = "123456";
}
