package com.xiao.framework.biz.redis;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;

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
    private static final int MAX_ACQUIRE_JEDIS_TIMES = 3;

    @Getter
    @Setter
    private LinkedList<RedisWrapper> alternatives;
    private JedisPool jedisPool;

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
        // get Jedis instance
        return handleJedis(this.jedisPool);
    }

    /**
     * Get jedis instance from master jedisPool or try alternatives when try master failed.
     */
    @NotNull
    private Jedis handleJedis(JedisPool jedisPool) throws JedisException {
        Jedis jedis;
        try {
            jedis = getJedisFromPool(jedisPool);
        } catch (JedisException ex) {
            // this means jedisPool can't get a good jedis instance.
            jedis = tryAlternativeJedis();
            if (jedis == null) {
                throw new JedisException(ex.getMessage() + " and have no available alternatives", ex);
            }
        }
        return jedis;
    }

    /**
     * Get jedis instance from master jedis pool.
     */
    @NotNull
    private Jedis getJedisFromPool(JedisPool jedisPool) throws JedisException {
        Jedis jedis;
        // if we meet exception while create jedis
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException ex) {
            // connection exceptions
            throw new JedisConnectionException("Connection exception when get jedis, "
                    + "host: " + this.master.getHost() + " port: " + this.master.getPort(), ex);
        } catch (JedisExhaustedPoolException ex) {
            // exhausted pool
            jedis = retryAcquireJedis();
            if (jedis == null) {
                throw new JedisExhaustedPoolException("Exhausted exception when get jedis, "
                        + "host: " + this.master.getHost() + " port: " + this.master.getPort(), ex);
            }
        } catch (JedisException ex) {
            // active or validate exception
            throw new JedisException("Active or validate exception when get jedis, "
                    + "host: " + this.master.getHost() + " port: " + this.master.getPort(), ex);
        }
        return jedis;
    }

    /**
     * try to init jedis pool and get jedis instance from alternatives.
     */
    private Jedis tryAlternativeJedis() {
        Jedis jedis = null;
        // make a copy
        RedisWrapper origin = RedisWrapper.builder()
                .host(this.master.getHost())
                .port(this.master.getPort()).build();
        LinkedList<RedisWrapper> originAlternatives = deepCopy(this.alternatives);
        if (CollectionUtils.isNotEmpty(this.alternatives)) {
            jedis = tryGetAlternativeJedis(this.master);
        }
        // can't create valid jedis from alternatives, recover all.
        if (jedis == null) {
            this.master = origin;
            this.alternatives = originAlternatives;
        }
        return jedis;
    }

    private Jedis tryGetAlternativeJedis(@NotNull RedisWrapper origin) {
        this.alternatives.add(this.master);
        // means already looped once
        if (Objects.equals(origin, this.alternatives.element())) {
            return null;
        }
        // clear jedis pool
        this.jedisPool = null;
        this.master = this.alternatives.poll();
        Jedis jedis;
        try {
            jedis = getJedisFromAlternatives();
        } catch (JedisException ex) {
            jedis = null;
        }
        if (jedis == null) {
            return tryGetAlternativeJedis(origin);
        }
        return jedis;
    }

    private Jedis getJedisFromAlternatives() throws JedisException {
        Jedis jedis;
        getJedisPool();
        jedis = getJedisFromPool(this.jedisPool);
        if (jedis != null) {
            jedis.slaveofNoOne();
        }
        return jedis;
    }

    private Jedis retryAcquireJedis() {
        int retryTimes = 0;
        Jedis jedis = null;
        while (retryTimes++ < MAX_ACQUIRE_JEDIS_TIMES) {
            try {
                jedis = this.jedisPool.getResource();
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
            this.jedisPool = JedisUtil.getJedisPool(this.master.getHost(), this.master.getPort(),
                    this.master.getPassword());
        }
    }

    private LinkedList<RedisWrapper> deepCopy(LinkedList<RedisWrapper> slaves) {
        if (slaves == null) {
            return null;
        }
        LinkedList<RedisWrapper> copy = new LinkedList<>();
        slaves.forEach(slave -> copy.add(RedisWrapper.builder().host(slave.getHost()).port(slave.getPort()).build()));
        return copy;
    }
}
