package com.xiao.framework.redis.jedis;

import com.xiao.framework.redis.exception.JedisCustomException;
import com.xiao.framework.redis.exception.JedisCustomException.ConnectionException;
import com.xiao.framework.redis.exception.JedisCustomException.ExhaustedPoolException;
import com.xiao.framework.redis.exception.JedisCustomException.ValidationException;
import com.xiao.framework.redis.utils.JedisPoolUtil;
import com.xiao.framework.redis.utils.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Manager of jedis.
 *
 * @author lix wang
 */
public class JedisManagerHelper {
    private static final int MAX_TOTAL_MASTER = 5;
    private static final int MAX_TOTAL_SLAVE = 10;
    private static final int MAX_ACQUIRE_JEDIS_TIMES = 3;

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

    public static void setSlaveOfMaster(@NotNull RedisWrapper master, @NotNull List<Jedis> slaves) {
        slaves.forEach(slave -> slave.slaveof(master.getHost(), master.getPort()));
    }

    public static void setSlaves(@NotNull JedisRefreshResult jedisRefreshResult,
            @NotNull LinkedList<RedisWrapper> potentialSlaves) {
        List<JedisManagerWrapper> realSlaves = new ArrayList<>();
        LinkedList<RedisWrapper> wrongSlaves = new LinkedList<>();
        List<Jedis> slaveJedisList = new ArrayList<>();
        potentialSlaves.forEach(slave -> {
            JedisPool slavePool = JedisManagerHelper.getJedisPoolForMaster(slave);
            try {
                Jedis jedis = JedisManagerHelper.getJedisFromPool(slavePool);
                slaveJedisList.add(jedis);
            } catch (ConnectionException | ExhaustedPoolException | ValidationException ex) {
                wrongSlaves.add(slave);
            }
            realSlaves.add(new JedisManagerWrapper(slavePool, slave));
        });
        jedisRefreshResult.setSlaves(realSlaves);
        jedisRefreshResult.setWrongSlaves(wrongSlaves);
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
                    Thread.sleep(100);
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
