package com.xiao.framework.redis.jedis;

import com.xiao.framework.redis.exception.JedisCustomException.ConnectionException;
import com.xiao.framework.redis.exception.JedisCustomException.ExhaustedPoolException;
import com.xiao.framework.redis.exception.JedisCustomException.ValidationException;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Manager of jedis slave.
 *
 * @author lix wang
 */
public class JedisSlaveManager {
    private List<JedisManagerWrapper> jedisManagerWrappers;

    private final JedisManager jedisManager;

    public JedisSlaveManager(JedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    public void setJedisManagerWrappers(List<JedisManagerWrapper> jedisManagerWrappers) {
        this.jedisManagerWrappers = jedisManagerWrappers;
    }

    /**
     * Get jedis from slave.
     *
     * @return
     */
    public Jedis getJedis() {
        for (JedisManagerWrapper jedisManagerWrapper : jedisManagerWrappers) {
            try {
                return JedisManagerHelper.getJedisFromPool(jedisManagerWrapper.getJedisPool());
            } catch (ConnectionException | ExhaustedPoolException | ValidationException ex) {
                jedisManager.reportWrongJedis(jedisManagerWrapper.getRedisWrapper());
            }
        }
        return null;
    }
}
