package com.xiao.framework.biz.redis;

import javax.validation.constraints.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Helper for redis.
 *
 * Note that I give up to use spring-data-redis.
 * I tried to use RedisTemplate through spring-data-redis. But, I found that spring-data-redis imported
 * many outer dependencies which from jedis.
 * If your jedis version is not suit your spring-data-redis properly, it would cause many unexpected exceptions,
 * so I give up to use RedisTemplate.
 *
 * @author lix wang
 */
public class RedisHelper {
    public static RedisService getRedisService(@NotNull String host, int port, String password) {
        InvocationHandler handler = new JedisProxy(host, port, password);
        return (RedisService) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                new Class[]{RedisService.class}, handler);
    }
}
