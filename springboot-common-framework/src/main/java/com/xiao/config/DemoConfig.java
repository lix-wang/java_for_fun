package com.xiao.config;

import com.xiao.biz.environment.EnvConfig;
import com.xiao.biz.environment.LixConfiguration;
import com.xiao.biz.environment.ProfileType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lix wang
 */
@LixConfiguration
public class DemoConfig {
    @EnvConfig(
            environments = ProfileType.PROD,
            value = "PROD value")
    @EnvConfig(
            environments = ProfileType.BETA,
            value = "BETA value")
    @EnvConfig(
            environments = {
                    ProfileType.DEV,
                    ProfileType.ALPHA},
            value = "DEV or ALPHA value")
    @Getter
    @Setter
    private String demoField = "init value";
}
