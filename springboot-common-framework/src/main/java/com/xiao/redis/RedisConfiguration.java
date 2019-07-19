package com.xiao.redis;

import com.sun.tools.javac.util.Assert;
import com.xiao.config.CommonConfig;
import com.xiao.framework.biz.redis.RedisHelper;
import com.xiao.framework.biz.redis.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Configuration for redis.
 *
 * @author lix wang
 */
@Lazy
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisService createDefaultRedis(CommonConfig commonConfig) {
        String[] redisItems = commonConfig.getRedisUrl().split(":");
        Assert.check(redisItems.length >= 2, "Must config host and port for redis url at least.");
        String host = redisItems[0];
        int port = Integer.parseInt(redisItems[1]);
        String password = redisItems.length >= 3 ? redisItems[2] : null;
        return RedisHelper.getRedisService(host, port, password);
    }
}
