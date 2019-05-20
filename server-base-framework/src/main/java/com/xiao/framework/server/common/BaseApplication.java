package com.xiao.framework.server.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author lix wang
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
public abstract class BaseApplication {
    protected static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
        return new SpringApplication(primarySource).run(args);
    }
}
