package com.xiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *
 * @author lix wang
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
public class SpringDemoServer {
    public static void main(String[] args) {
        SpringApplication.run(SpringDemoServer.class);
    }
}
