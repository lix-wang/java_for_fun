package com.xiao.framework.biz.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Impl for Redis service.
 *
 * @author lix wang
 */
public class RedisTemplateBaseRedisService<K, V> implements BaseRedisService<K, V> {
    private final RedisTemplate<K, V> redisTemplate;

    public RedisTemplateBaseRedisService(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean exists(K key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean remove(K key) {
        return redisTemplate.delete(key);
    }

    @Override
    public boolean expire(K key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setex(K key, long seconds, V value) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }
}
