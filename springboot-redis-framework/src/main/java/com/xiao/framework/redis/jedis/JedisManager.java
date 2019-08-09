package com.xiao.framework.redis.jedis;

import com.sun.tools.javac.util.Assert;
import com.xiao.framework.base.utils.JsonUtil;
import com.xiao.framework.redis.exception.JedisCustomException;
import com.xiao.framework.redis.exception.JedisCustomException.ConnectionException;
import com.xiao.framework.redis.exception.JedisCustomException.ExhaustedPoolException;
import com.xiao.framework.redis.exception.JedisCustomException.NoValidJedis;
import com.xiao.framework.redis.exception.JedisCustomException.ValidationException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manager of jedis.
 *
 * @author lix wang
 */
@Log4j2
public class JedisManager {
    private static final long REFRESH_SLAVE_DURATION = 10000;
    private long lastSlaveRefreshTime = 0;

    @Setter
    private LinkedList<RedisWrapper> slaves;
    private RedisWrapper master;
    private LinkedList<RedisWrapper> wrongSlaves;

    private JedisMasterManager jedisMasterManager;
    private JedisSlaveManager jedisSlaveManager;

    public JedisManager(RedisWrapper master) {
        this(master, null);
    }

    public JedisManager(RedisWrapper master, LinkedList<RedisWrapper> slaves) {
        this.master = master;
        this.slaves = slaves;
        this.jedisMasterManager = new JedisMasterManager(this);
        this.jedisSlaveManager = new JedisSlaveManager(this);
        refreshMasterManagers();
        refreshSlaveManager(true);

    }

    public Jedis getJedis(boolean isSlaveOp) throws NoValidJedis {
        boolean needRefreshMaster = false;
        boolean needRefreshSlaves = false;
        Jedis jedis = null;
        try {
            // have slaves.
            if (isSlaveOp && CollectionUtils.isNotEmpty(this.slaves)) {
                jedis = this.jedisSlaveManager.getJedis();
            }
            // get jedis from master.
            if ((!isSlaveOp || jedis == null) && this.master != null) {
                jedis = this.jedisMasterManager.getJedis();
                needRefreshMaster = jedis == null;
            }
            needRefreshSlaves = CollectionUtils.isNotEmpty(wrongSlaves);
            if (jedis != null) {
                return jedis;
            }
            throw JedisCustomException.noValidJedis();
        } finally {
            if (needRefreshMaster) {
                refreshMasterManagers();
            }
            if (needRefreshSlaves) {
                refreshSlaveManager(false);
            }
        }
    }

    public void reportWrongJedis(@NotNull RedisWrapper redisWrapper) {
        moveToWrong(redisWrapper);
    }

    /**
     * Refresh master manager.
     */
    private void refreshMasterManagers() {
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

    /**
     * Refresh slave manager.
     */
    private void refreshSlaveManager(boolean forceRefresh) {
        if (!forceRefresh && (lastSlaveRefreshTime + REFRESH_SLAVE_DURATION) > System.currentTimeMillis()) {
            return;
        }
        // make sure master is valid.
        Assert.check(this.jedisMasterManager.checkMasterValid(), "Jedis master invalid, refresh slaves failed.");
        LinkedList<RedisWrapper> potential = potentialWrappers();
        List<JedisManagerWrapper> slaves = new ArrayList<>();
        LinkedList<RedisWrapper> wrongSlaves = new LinkedList<>();
        potential.forEach(redisWrapper -> {
            JedisManagerWrapper managerWrapper = JedisManagerHelper.checkSlave(redisWrapper,
                    this.jedisMasterManager.getJedisManagerWrapper().getRedisWrapper());
            if (managerWrapper == null) {
                wrongSlaves.add(redisWrapper);
            } else {
                slaves.add(managerWrapper);
            }
        });
        this.slaves = slaves.stream()
                .map(JedisManagerWrapper::getRedisWrapper)
                .collect(Collectors.toCollection(LinkedList::new));
        this.wrongSlaves = wrongSlaves;
        this.jedisSlaveManager.setJedisManagerWrappers(slaves);
        this.lastSlaveRefreshTime = System.currentTimeMillis();
    }

    /**
     * Find a valid slave improve to master.
     *
     * @return
     */
    private JedisManagerWrapper findValidSlaveAsMaster() {
        // check from slaves.
        JedisManagerWrapper jedisManagerWrapper;
        jedisManagerWrapper = findValidAsMaster(this.slaves);
        if (jedisManagerWrapper != null) {
            return jedisManagerWrapper;
        }
        // check from wrong slaves.
        jedisManagerWrapper = findValidAsMaster(this.wrongSlaves);
        return jedisManagerWrapper;
    }

    private JedisManagerWrapper findValidAsMaster(@NotNull LinkedList<RedisWrapper> redisWrappers) {
        for (RedisWrapper redisWrapper : redisWrappers) {
            JedisManagerWrapper managerWrapper = JedisManagerHelper.checkMaster(redisWrapper);
            if (managerWrapper != null) {
                // if found a valid master, then move current master to slaves.
                moveToWrong(this.master);
                this.master = managerWrapper.getRedisWrapper();
                redisWrappers.remove(redisWrapper);
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

    private void moveToWrong(@NotNull RedisWrapper redisWrapper) {
        // get a valid slave
        if (this.wrongSlaves == null) {
            this.wrongSlaves = new LinkedList<RedisWrapper>() {
                {
                    add(redisWrapper);
                }
            };
        } else {
            this.wrongSlaves.add(redisWrapper);
        }
        log.error(String.format("Wrong redis list: %s", JsonUtil.serialize(this.wrongSlaves)));
    }
}
