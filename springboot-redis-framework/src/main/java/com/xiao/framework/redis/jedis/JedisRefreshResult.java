package com.xiao.framework.redis.jedis;

import lombok.Data;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

/**
 * Jedis refresh result.
 *
 * @author lix wang
 */
@Data
public class JedisRefreshResult {
    private JedisManagerWrapper master;
    private List<JedisManagerWrapper> slaves;
    private LinkedList<RedisWrapper> wrongSlaves;
    private List<Jedis> slaveJedisList;
}
