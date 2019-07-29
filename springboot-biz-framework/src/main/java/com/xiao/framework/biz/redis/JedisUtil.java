package com.xiao.framework.biz.redis;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import javax.validation.constraints.NotNull;

import java.net.URI;

/**
 * Jedis util.
 *
 * only make it be used in this package.
 *
 * @author lix wang
 */
class JedisUtil {
    private static final int DEFAULT_MAX_TOTAL = 2;
    private static final int DEFAULT_MAX_IDLE = 2;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_READ_TIMEOUT = 5000;

    static JedisPool getJedisPool(@NotNull String host, int port, String password) {
        // this timeout means connection timeout and socket inputStream read ReadTimeout
        return new JedisPool(getPoolConfig(DEFAULT_MAX_TOTAL, DEFAULT_MAX_IDLE), createRedisURI(host, port,
                Protocol.DEFAULT_DATABASE, password), DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    private static GenericObjectPoolConfig getPoolConfig(int maxTotal, int maxIdle) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // minimum time of one Jedis instance can be set idle. this time used by idle object evictor.
        // if non-positive then no objects will be evicted.
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        // time break of idle object evictor thread. when non-positive no idle object evictor thread will run.
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);
        // maximum of objects to examine during each run of idle object evictor thread.
        poolConfig.setNumTestsPerEvictionRun(-1);
        // default true but maxWaitMillis is -1, so it will not effect actually.
        poolConfig.setBlockWhenExhausted(false);
        // when get a jedis instance, it will do activation and validation.
        poolConfig.setTestOnBorrow(true);
        // maximum jedis instances.
        poolConfig.setMaxTotal(maxTotal);
        // means maximum of jedis instances that can exist. When it is eligible for evict, it will be release.
        poolConfig.setMaxIdle(maxIdle);
        // if it is not greater than zero, actually, it is not effect.
        poolConfig.setMinIdle(0);
        return poolConfig;
    }

    private static URI createRedisURI(@NotNull String host, int port, int database, String password) {
        String defaultFormat = "redis://%s:%d?db=%d";
        String uriStr = StringUtils.isBlank(password)
                ? String.format(defaultFormat, host, port, database)
                : String.format(defaultFormat + "&password=%s", host, port, database, password);
        return URI.create(uriStr);
    }
}
