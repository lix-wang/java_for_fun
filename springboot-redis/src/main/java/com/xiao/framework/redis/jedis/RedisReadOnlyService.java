package com.xiao.framework.redis.jedis;

import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * Service of redis read.
 *
 * @author lix wang
 */
public interface RedisReadOnlyService {
    String get(String key);

    Boolean exists(String key);

    String watch(String... keys);

    String unwatch();

    Long ttl(String key);

    Long zrank(String key, String member);

    Set<Tuple> zrangeWithScores(String key, long start, long stop);
}
