package com.xiao.framework.server.undertow;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom UndertowServletWebServerFactory
 *
 * @author lix wang
 */
@Configuration
@AutoConfigureBefore(ServletWebServerFactoryAutoConfiguration.class)
public class CustomServletWebServerFactoryAutoConfiguration {
    @Bean
    public CustomUndertowServletWebServerFactory createServletWebServerFactory() {
        return new CustomUndertowServletWebServerFactory();
    }
}
