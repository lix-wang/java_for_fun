package com.xiao.framework.redis.jedis;

import com.xiao.framework.redis.exception.JedisCustomException.ConnectionException;
import com.xiao.framework.redis.exception.JedisCustomException.ExhaustedPoolException;
import com.xiao.framework.redis.exception.JedisCustomException.ValidationException;
import redis.clients.jedis.Jedis;

/**
 * Manager of jedis master.
 *
 * @author lix wang
 */
public class JedisMasterManager {
    private JedisManagerWrapper jedisManagerWrapper;

    private final JedisManager jedisManager;

    public JedisMasterManager(JedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    public Jedis getJedis() {
        try {
            return JedisManagerHelper.getJedisFromPool(jedisManagerWrapper.getJedisPool());
        } catch (ConnectionException | ExhaustedPoolException | ValidationException ex) {
            JedisManagerHelper.setMessage(ex, this.jedisManagerWrapper.getRedisWrapper());
            jedisManager.reportWrongJedis(this.jedisManagerWrapper.getRedisWrapper());
            return null;
        }
    }

    public boolean checkMasterValid() {
        Jedis jedis = null;
        try {
             jedis = getJedis();
             return jedis != null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setJedisManagerWrapper(JedisManagerWrapper jedisManagerWrapper) {
        this.jedisManagerWrapper = jedisManagerWrapper;
    }

    public JedisManagerWrapper getJedisManagerWrapper() {
        return this.jedisManagerWrapper;
    }
}
