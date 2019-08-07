package com.xiao.redis;

import com.xiao.framework.redis.jedis.RedisLock;
import com.xiao.framework.redis.jedis.RedisService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Factory to generate redis distribution lock
 *
 * @author lix wang
 */
@Configuration
public class RedisLockFactory {
    @Bean
    public RedisLock getDefaultRedisLock(@Qualifier("defaultRedis") RedisService defaultRedis) {
        return new RedisLock(defaultRedis);
    }
}
