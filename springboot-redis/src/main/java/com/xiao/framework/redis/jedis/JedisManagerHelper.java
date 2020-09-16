package com.xiao.framework.redis.jedis;

import com.xiao.framework.base.exception.LixException;
import com.xiao.framework.redis.exception.JedisCustomException;
import com.xiao.framework.redis.exception.JedisCustomException.ConnectionException;
import com.xiao.framework.redis.exception.JedisCustomException.ExhaustedPoolException;
import com.xiao.framework.redis.exception.JedisCustomException.ValidationException;
import com.xiao.framework.redis.utils.JedisPoolUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;

import javax.validation.constraints.NotNull;

/**
 * Manager of jedis.
 *
 * @author lix wang
 */
public class JedisManagerHelper {
    private static final int MAX_TOTAL_MASTER = 5;
    private static final int MAX_TOTAL_SLAVE = 10;
    private static final int MAX_ACQUIRE_JEDIS_TIMES = 3;
    private static final long EXHAUSTED_DURATION_MILLIS = 500;

    public static JedisPool getJedisPoolForMaster(@NotNull RedisWrapper redisWrapper) {
        return JedisPoolUtil.getJedisPool(redisWrapper.getHost(), redisWrapper.getPort(),
                redisWrapper.getPassword(), MAX_TOTAL_MASTER);
    }

    public static JedisPool getJedisPoolForSlave(@NotNull RedisWrapper redisWrapper) {
        return JedisPoolUtil.getJedisPool(redisWrapper.getHost(), redisWrapper.getPort(), redisWrapper.getPassword(),
                MAX_TOTAL_SLAVE);
    }

    /**
     * Get jedis instance from master jedis pool.
     */
    @NotNull
    public static Jedis getJedisFromPool(@NotNull JedisPool jedisPool)
            throws ConnectionException, ExhaustedPoolException, ValidationException {
        Jedis jedis;
        // if we meet exception while create jedis
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException ex) {
            // connection exceptions
            throw JedisCustomException.connectionException();
        } catch (JedisExhaustedPoolException ex) {
            // exhausted pool
            jedis = retryAcquireJedis(jedisPool);
            if (jedis == null) {
                throw JedisCustomException.exhaustedPoolException();
            }
        } catch (JedisException ex) {
            // active or validate exception
            throw JedisCustomException.validationException();
        }
        return jedis;
    }

    public static JedisManagerWrapper checkMaster(@NotNull RedisWrapper redisWrapper) {
        JedisPool jedisPool = getJedisPoolForMaster(redisWrapper);
        try {
            Jedis jedis = getJedisFromPool(jedisPool);
            jedis.slaveofNoOne();
            return new JedisManagerWrapper(jedisPool, redisWrapper);
        } catch (ConnectionException | ExhaustedPoolException | ValidationException ex) {
            return null;
        }
    }

    public static JedisManagerWrapper checkSlave(@NotNull RedisWrapper potentialSlave, @NotNull RedisWrapper master) {
        JedisPool jedisPool = getJedisPoolForSlave(potentialSlave);
        try {
            Jedis jedis = getJedisFromPool(jedisPool);
            jedis.slaveof(master.getHost(), master.getPort());
            return new JedisManagerWrapper(jedisPool, potentialSlave);
        } catch (ConnectionException | ExhaustedPoolException | ValidationException ex) {
            return null;
        }
    }

    public static void setMessage(@NotNull LixException ex, @NotNull RedisWrapper redisWrapper) {
        ex.setMessage(ex.getMessage() + " host: " + redisWrapper.getHost() + " port: " + redisWrapper.getPort());
    }

    /**
     * retry to get jedis instance
     */
    private static Jedis retryAcquireJedis(@NotNull JedisPool jedisPool) {
        int retryTimes = 0;
        Jedis jedis = null;
        while (retryTimes++ < MAX_ACQUIRE_JEDIS_TIMES) {
            try {
                jedis = jedisPool.getResource();
            } catch (Exception e) {
                jedis = null;
                try {
                    Thread.sleep(EXHAUSTED_DURATION_MILLIS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if (jedis != null) {
                return jedis;
            }
        }
        return jedis;
    }
}
