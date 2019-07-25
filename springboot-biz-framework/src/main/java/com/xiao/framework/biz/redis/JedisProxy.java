package com.xiao.framework.biz.redis;

import com.xiao.framework.biz.exception.LixRuntimeException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.commands.JedisCommands;

import javax.validation.constraints.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Proxy for jedis.
 *
 * @author lix wang
 */
public class JedisProxy implements InvocationHandler {
    private Logger logger = LogManager.getLogger(JedisProxy.class);
    private static final int MAX_ACQUIRE_JEDIS_TIMES = 3;

    @Getter
    @Setter
    private LinkedList<RedisWrapper> slaves;
    private LixJedisPool jedisPool;

    private RedisWrapper master;

    public JedisProxy(RedisWrapper master) {
        this.master = master;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // get real target method
        Method realMethod = JedisCommands.class.getMethod(method.getName(), method.getParameterTypes());
        Object result;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            result = realMethod.invoke(jedis, args);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    @NotNull
    private Jedis getJedis() {
        getJedisPool();
        if (this.jedisPool != null) {
            // get Jedis instance
            Jedis jedis = handleJedis(this.jedisPool);
            if (jedis != null) {
                return jedis;
            }
        }
        throw LixRuntimeException.builder()
                .errorCode("jedis.get_jedis_instance_failed")
                .message("Get jedis from jedis pool failed").build();
    }

    private Jedis handleJedis(LixJedisPool jedisPool) {
        Jedis jedis = getJedisFromPool(jedisPool);
        // this means jedisPool can't get a good jedis instance.
        if (CollectionUtils.isNotEmpty(this.slaves)) {
            if (jedis == null) {
                jedis = trySlaveJedis();
            }
            if (jedis != null) {
                for (RedisWrapper wrapper : this.slaves) {
                    jedis.slaveof(wrapper.getHost(), wrapper.getPort());
                }
            }
        }
        return jedis;
    }

    private Jedis getJedisFromPool(LixJedisPool jedisPool) {
        Jedis jedis = null;
        // if we meet exception while create jedis
        try {
            jedis = jedisPool.getJedisResource();
        } catch (NoSuchMethodException e) {
            if (null == e.getCause()) {
                // the exception was caused by an exhausted pool.
                jedis = retryAcquireJedis();
            }
            // otherwise the exception was caused by activateObject() or validateObject()
            logger.error("Validation failed when get jedis from master host: " + this.master.getHost()
                    + " port: " + this.master.getPort(), e);
        } catch (Exception e) {
            // other unexpected exceptions
            logger.error("Unexpected exception when get good jedis from master host: " + this.master.getHost()
                    + " port: " + this.master.getPort(), e);
            jedis = null;
        }
        return jedis;
    }

    private Jedis trySlaveJedis() {
        if (CollectionUtils.isNotEmpty(this.slaves)) {
            return tryGetJedis(this.master);
        }
        return null;
    }

    private Jedis tryGetJedis(@NotNull RedisWrapper origin) {
        this.slaves.add(this.master);
        // means already looped once
        if (Objects.equals(origin, this.slaves.element())) {
            return null;
        }
        // clear jedis pool
        this.jedisPool = null;
        this.master = this.slaves.poll();
        Jedis jedis = getJedisFromSlave();
        if (jedis == null) {
            return tryGetJedis(origin);
        }
        return jedis;
    }

    private Jedis getJedisFromSlave() {
        Jedis jedis = null;
        getJedisPool();
        if (this.jedisPool != null) {
            jedis = getJedisFromPool(this.jedisPool);
            if (jedis != null) {
                jedis.slaveofNoOne();
            }
        }
        return jedis;
    }

    private Jedis retryAcquireJedis() {
        int retryTimes = 0;
        Jedis jedis = null;
        while (retryTimes++ < MAX_ACQUIRE_JEDIS_TIMES) {
            try {
                jedis = this.jedisPool.getJedisResource();
            } catch (Exception e) {
                jedis = null;
            }
            if (jedis != null) {
                return jedis;
            }
        }
        return jedis;
    }

    private void getJedisPool() {
        if (this.jedisPool == null) {
            this.jedisPool = JedisUtil.getLixJedisPool(this.master.getHost(), this.master.getPort(),
                    this.master.getPassword());
        }
    }
}
