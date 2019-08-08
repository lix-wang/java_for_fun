package com.xiao.framework.redis.jedis;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Manager of jedis slave.
 *
 * @author lix wang
 */
public class JedisSlaveManager {
    private List<JedisManagerWrapper> jedisManagerWrappers;

    public void setJedisManagerWrappers(List<JedisManagerWrapper> jedisManagerWrappers) {
        this.jedisManagerWrappers = jedisManagerWrappers;
    }

    public Jedis getJedis() {
        return null;
    }
}
