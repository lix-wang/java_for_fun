package com.xiao.framework.biz.redis;

import com.xiao.framework.biz.exception.LixRuntimeException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.util.Pool;

/**
 *
 * @author lix wang
 */
public class LixJedisPool extends Pool<Jedis> {
    private final JedisPoolAbstract jedisPoolAbstract;

    public LixJedisPool(final JedisPoolAbstract jedisPoolAbstract,
            final GenericObjectPoolConfig poolConfig,
            PooledObjectFactory<Jedis> pooledObjectFactory) {
        super(poolConfig, pooledObjectFactory);
        this.jedisPoolAbstract = jedisPoolAbstract;
    }

    // Let jedis pool throw exception.
    public Jedis getJedisResource() throws Exception {
        Jedis jedis = internalPool.borrowObject();
        jedis.setDataSource(jedisPoolAbstract);
        return jedis;
    }

    /**
     * use {@link #getJedisResource()} instead
     */
    @Deprecated
    @Override
    public Jedis getResource() {
        throw LixRuntimeException.builder()
                .message("This method is not support yet.").build();
    }
}
