package com.xiao.framework.biz.redis;

/**
 * Service for redis.
 *
 * @author lix wang
 */
public interface BaseRedisService<K, V> {
    boolean exists(K key);

    boolean remove(K key);

    boolean expire(K key, long seconds);

    V get(K key);

    void set(K key, V value);

    void setex(K key, long seconds, V value);
}
