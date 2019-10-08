package com.xiao.demo.server.demo.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * @author lix wang
 */
public class StandardServletMultipartConfiguration {
    @Bean
    public MultipartResolver standardMultipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
