package com.xiao.framework.biz.redis;

/**
 * Redis service which only handle String.
 *
 * @author lix wang
 */
public interface RedisService {
    String get(String key);

    String set(String key, String value);

    String setex(String key, int seconds, String value);

    Boolean exists(String key);

    Long expire(String key, int seconds);

    Long del(String key);
}
