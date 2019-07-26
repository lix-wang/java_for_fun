package com.xiao.redis;

import com.xiao.config.CommonConfig;
import com.xiao.framework.biz.redis.RedisHelper;
import com.xiao.framework.biz.redis.RedisService;
import com.xiao.framework.biz.redis.RedisWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.LinkedList;

/**
 * Configuration for redis.
 *
 * @author lix wang
 */
@Lazy
@Configuration
public class RedisFactory {
    @Bean
    public RedisService createDefaultRedis(CommonConfig commonConfig) {
        RedisWrapper master = RedisWrapper.builder()
                .host("127.0.0.1")
                .port(6789).build();

        LinkedList<RedisWrapper> slaves = new LinkedList<RedisWrapper>() {
            {
                add(RedisWrapper.builder().host(commonConfig.getRedisHost()).port(commonConfig.getRedisPort()).build());
            }
        };
        return RedisHelper.getRedisService(master, slaves);
    }
}
