package com.xiao.framework.redis.jedis;

import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Proxy for jedis.
 *
 * @author lix wang
 */
@Log4j2
public class JedisProxy implements InvocationHandler {
    private final JedisManager jedisManager;

    public JedisProxy(JedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        // get real target method
        boolean slaveOpFlag;
        try {
            slaveOpFlag = RedisReadOnlyService.class.getDeclaredMethod(
                    method.getName(), method.getParameterTypes()) != null;
        } catch (NoSuchMethodException ex) {
            slaveOpFlag = false;
        }

        Method realMethod = Jedis.class.getMethod(method.getName(), method.getParameterTypes());
        Object result;
        try (Jedis jedis = jedisManager.getJedis(slaveOpFlag)) {
            result = realMethod.invoke(jedis, args);
            log.info(String.format("Execute %s consume: %d ms", method.getName(), (System.currentTimeMillis() - start)));
        }
        return result;
    }
}
