package com.xiao.framework.redis.jedis;

import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        Method realMethod = Jedis.class.getMethod(method.getName(), method.getParameterTypes());
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
