package com.xiao.redis;

import com.xiao.framework.redis.jedis.RedisService;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * Fake redisService while dev or unittest.
 */
public class FakeRedisService implements RedisService {
    @Override
    public String set(String key, String value) {
        return null;
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return null;
    }

    @Override
    public Long expire(String key, int seconds) {
        return null;
    }

    @Override
    public Long del(String key) {
        return null;
    }

    @Override
    public Long setnx(String key, String value) {
        return null;
    }

    @Override
    public Transaction multi() {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return null;
    }

    @Override
    public Long zrem(String key, String... members) {
        return null;
    }

    @Override
    public Long rpush(String key, String... string) {
        return null;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public Boolean exists(String key) {
        return null;
    }

    @Override
    public String watch(String... keys) {
        return null;
    }

    @Override
    public String unwatch() {
        return null;
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Long zrank(String key, String member) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        return null;
    }
}
