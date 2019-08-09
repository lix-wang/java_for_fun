package com.xiao.framework.redis.jedis;

import redis.clients.jedis.Transaction;

/**
 * Redis service which only handle String.
 *
 * <p>
 *     减少列表、集合、散列和有序集合的体积可以减少内存的占用。
 * @author lix wang
 */
public interface RedisService extends RedisSlaveService {
    String set(String key, String value);

    String setex(String key, int seconds, String value);

    Long expire(String key, int seconds);

    Long del(String key);

    Long setnx(String key, String value);

    Transaction multi();

    Long zremrangeByScore(String key, double min, double max);

    Long zadd(String key, double score, String member);

    Long zrem(String key, String... members);

    Long rpush(String key, String... string);
}
