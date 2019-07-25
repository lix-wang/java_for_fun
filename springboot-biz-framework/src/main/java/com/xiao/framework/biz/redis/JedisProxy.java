package com.xiao.framework.biz.redis;

import com.xiao.framework.biz.exception.LixRuntimeException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.commands.JedisCommands;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Proxy for jedis.
 *
 * @author lix wang
 */
public class JedisProxy implements InvocationHandler {
    private final String host;
    private final int port;
    private final String password;

    private JedisPool jedisPool;

    public JedisProxy(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
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

    private Jedis getJedis() {
        if (this.jedisPool == null) {
            this.jedisPool = JedisUtil.getJedisPool(host, port, password);
        }
        if (jedisPool != null) {
            return jedisPool.getResource();
        }
        throw LixRuntimeException.builder()
                .errorCode("jedis.get_jedis_instance_failed")
                .message("Get jedis from jedis pool failed").build();
    }
}
