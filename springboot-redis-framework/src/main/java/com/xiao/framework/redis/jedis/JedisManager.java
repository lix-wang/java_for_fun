package com.xiao.framework.redis.jedis;

import com.xiao.framework.redis.exception.JedisCustomException;
import com.xiao.framework.redis.exception.JedisCustomException.ConnectionException;
import com.xiao.framework.redis.exception.JedisCustomException.ExhaustedPoolException;
import com.xiao.framework.redis.exception.JedisCustomException.NoValidJedis;
import com.xiao.framework.redis.exception.JedisCustomException.ValidationException;
import lombok.Setter;
import redis.clients.jedis.JedisPool;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Manager of jedis.
 *
 * @author lix wang
 */
public class JedisManager {
    @Setter
    private LinkedList<RedisWrapper> slaves;
    private RedisWrapper master;
    private LinkedList<RedisWrapper> wrongSlaves;

    private JedisMasterManager jedisMasterManager;
    private JedisSlaveManager jedisSlaveManager;

    public JedisManager(RedisWrapper master) {
        this.master = master;
        this.jedisMasterManager = new JedisMasterManager();
        this.jedisSlaveManager = new JedisSlaveManager();
        refreshMasterManagers();
        refreshSlaveManager();
    }

    /**
     * Refresh managers.
     */
    void refreshMasterManagers() {
        JedisManagerWrapper masterManagerWrapper;
        try {
            // check master
            JedisPool masterPool = JedisManagerHelper.getJedisPoolForMaster(this.master);
            JedisManagerHelper.getJedisFromPool(masterPool);
            masterManagerWrapper = new JedisManagerWrapper(masterPool, this.master);
        } catch (ConnectionException | ExhaustedPoolException | ValidationException ex) {
            // master jedis is invalid
            masterManagerWrapper = findValidSlaveAsMaster();
        }
        // set master and slave manager
        if (jedisSlaveManager == null) {
            try {
                throw JedisCustomException.noValidJedis();
            } catch (NoValidJedis ex) {
                ex.toRuntimeException();
            }
        }
        this.jedisMasterManager.setJedisManagerWrapper(masterManagerWrapper);
    }

    // todo
    void refreshSlaveManager() {

    }

    /**
     * Find a valid slave improve to master.
     *
     * @return
     */
    private JedisManagerWrapper findValidSlaveAsMaster() {
        LinkedList<RedisWrapper> potential = potentialWrappers();
        // get a valid slave
        if (this.wrongSlaves == null) {
            this.wrongSlaves = new LinkedList<RedisWrapper>() {
                {
                    add(master);
                }
            };
        } else {
            this.wrongSlaves.add(this.master);
        }
        this.master = null;
        for (RedisWrapper redisWrapper : potential) {
            JedisManagerWrapper managerWrapper = JedisManagerHelper.checkMaster(redisWrapper);
            if (managerWrapper != null) {
                this.master = managerWrapper.getRedisWrapper();
                return managerWrapper;
            }
        }
        return null;
    }

    private LinkedList<RedisWrapper> potentialWrappers() {
        LinkedList<RedisWrapper> potential = new LinkedList<RedisWrapper>() {
            {
                addAll(Optional.ofNullable(slaves).orElse(new LinkedList<>()));
                addAll(Optional.ofNullable(wrongSlaves).orElse(new LinkedList<>()));
            }
        };
        return potential;
    }
}
