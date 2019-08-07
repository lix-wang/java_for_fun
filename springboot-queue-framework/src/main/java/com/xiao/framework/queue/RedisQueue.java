package com.xiao.framework.queue;

import com.xiao.framework.redis.jedis.RedisService;

/**
 * Custom redis task queue.
 *
 * @author lix wang
 */
public class RedisQueue {
    private static final String KEY_REDIS_QUEUE = "REDIS_QUEUE";

    private final RedisService redisService;

    public RedisQueue(RedisService redisService) {
        this.redisService = redisService;
    }

}
