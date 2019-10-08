package com.xiao.demo.server.demo.lambda;

import com.xiao.framework.redis.jedis.RedisService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
public class RedisServiceAdapterConfiguration {
    /**
     * Create a RedisServiceAdapter with lambda.
     *
     * @param redisService
     * @return
     */
    @Bean
    public RedisServiceAdapter createRedisAdapter(@Qualifier("defaultRedis") RedisService redisService) {
        RedisServiceAdapter adapter = K -> redisService.exists(K);
        return adapter;
    }
}
