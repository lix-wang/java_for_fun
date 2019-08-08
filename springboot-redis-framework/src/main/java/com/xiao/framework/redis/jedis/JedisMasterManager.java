package com.xiao.framework.redis.jedis;

import com.xiao.framework.base.exception.LixException;
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

    public Jedis getJedis() throws ConnectionException, ValidationException, ExhaustedPoolException {
        try {
            return JedisManagerHelper.getJedisFromPool(jedisManagerWrapper.getJedisPool());
        } catch (ConnectionException e) {
            setMessage(e);
            throw e;
        } catch (ExhaustedPoolException e) {
            setMessage(e);
            throw e;
        } catch (ValidationException e) {
            setMessage(e);
            throw e;
        }
    }

    public void setJedisManagerWrapper(JedisManagerWrapper jedisManagerWrapper) {
        this.jedisManagerWrapper = jedisManagerWrapper;
    }

    private void setMessage(LixException ex) {
        ex.setMessage(ex.getMessage() + " host: " + this.jedisManagerWrapper.getRedisWrapper().getHost()
                + " port: " + this.jedisManagerWrapper.getRedisWrapper().getPort());
    }
}
