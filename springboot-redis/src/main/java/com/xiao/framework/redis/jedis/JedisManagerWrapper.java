package com.xiao.framework.redis.jedis;

import lombok.AllArgsConstructor;
import lombok.Data;
import redis.clients.jedis.JedisPool;

/**
 * Jedis slave wrapper.
 *
 * @author lix wang
 */
@Data
@AllArgsConstructor
public class JedisManagerWrapper {
    private JedisPool jedisPool;
    private RedisWrapper redisWrapper;
}
