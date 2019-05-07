package com.xiao.environment;

import com.xiao.config.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author lix wang
 */
@Log4j2
@Configuration
public class CmdLineConfigProvider {
    @Value("${" + Constants.PROFILE_PROPERTY_NAME + "}")
    private String profile = "default,dev";

    @Value("${server.port}")
    private int port = Constants.DEFAULT_PORT;

    @Value("${com.server-name}")
    private String serverName = "unspecified";

    @Bean
    public CmdLineConfig initCmdLineConfig() {
        CmdLineConfig cmdLineConfig = new CmdLineConfig();
        cmdLineConfig.setProfile(ProfileType.getProfile(profile));
        cmdLineConfig.setHostName(getHostName());
        cmdLineConfig.setServerName(serverName);
        return cmdLineConfig;
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("Get hostName failed. ", e);
            return "localhost";
        }
    }
}
