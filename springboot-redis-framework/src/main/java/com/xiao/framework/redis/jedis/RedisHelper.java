package com.xiao.framework.redis.jedis;

import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotNull;

import java.lang.reflect.Proxy;
import java.util.LinkedList;

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
    public static RedisService getRedisService(@NotNull RedisWrapper master) {
        return getRedisService(master, null);
    }

    public static RedisService getRedisService(@NotNull RedisWrapper master, LinkedList<RedisWrapper> slaves) {
        JedisManager jedisManager = new JedisManager(master);
        if (CollectionUtils.isNotEmpty(slaves)) {
            jedisManager.setSlaves(slaves);
        }
        JedisProxy jedisProxy = new JedisProxy(jedisManager);
        return (RedisService) Proxy.newProxyInstance(jedisProxy.getClass().getClassLoader(),
                new Class[]{RedisService.class}, jedisProxy);
    }
}
