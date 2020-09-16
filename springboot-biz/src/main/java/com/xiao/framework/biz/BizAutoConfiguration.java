package com.xiao.framework.biz;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author lix wang
 */
@Configuration
@ComponentScan
@PropertySource("classpath:biz.properties")
public class BizAutoConfiguration {
}
