package com.xiao.framework.biz.redis;

import redis.clients.jedis.Transaction;

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

    Long setnx(String key, String value);

    String watch(String... keys);

    Transaction multi();

    String unwatch();

    Long ttl(String key);

    Long zremrangeByScore(String key, double min, double max);

    Long zadd(String key, double score, String member);

    Long zrank(String key, String member);

    Long zrem(String key, String... members);
}
