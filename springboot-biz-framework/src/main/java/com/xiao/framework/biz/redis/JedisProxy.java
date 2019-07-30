package com.xiao.framework.biz.redis;

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
    private final JedisManager jedisManager;

    public JedisProxy(JedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // get real target method
        Method realMethod = JedisCommands.class.getMethod(method.getName(), method.getParameterTypes());
        Object result;
        Jedis jedis = null;
        try {
            jedis = jedisManager.getJedis();
            result = realMethod.invoke(jedis, args);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }
}
