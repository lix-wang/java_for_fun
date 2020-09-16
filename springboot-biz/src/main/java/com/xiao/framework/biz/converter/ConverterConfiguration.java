package com.xiao.framework.biz.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for Converts
 * Just for practice, actually useless.
 *
 * @author lix wang
 */
// @Configuration
public class ConverterConfiguration {
    @Bean
    @Primary
    public DateTimeResponseConverter createDateTimeResponseConverter() {
        return new DateTimeResponseConverter();
    }
}
