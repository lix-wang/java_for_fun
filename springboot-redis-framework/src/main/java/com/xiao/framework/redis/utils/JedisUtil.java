package com.xiao.framework.redis.utils;

import com.xiao.framework.redis.jedis.RedisWrapper;

import javax.validation.constraints.NotNull;

/**
 * Jedis util.
 *
 * @author lix wang
 */
public class JedisUtil {
    public static RedisWrapper copyRedisWrapper(@NotNull RedisWrapper redisWrapper) {
        return RedisWrapper.builder()
                .host(redisWrapper.getHost())
                .port(redisWrapper.getPort())
                .password(redisWrapper.getPassword()).build();
    }
}
