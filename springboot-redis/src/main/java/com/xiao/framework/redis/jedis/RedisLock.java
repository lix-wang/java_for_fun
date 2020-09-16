package com.xiao.framework.redis.jedis;

import com.xiao.framework.redis.exception.RedisException;
import com.xiao.framework.redis.exception.RedisException.AcquireLockException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Distribution lock implements by redis.
 *
 * @author lix wang
 */
public class RedisLock {
    private static final String KEY_REDIS_LOCK = "REDIS_LOCK";

    @Setter
    private int lockExpireSeconds = 60;
    @Setter
    private long acquireDurationMillis = 100;
    @Setter
    private int fairSemaphoreCompetitionTimeout = 1;
    @Getter
    private long timeoutMillis = 5000;

    private final RedisService redisService;

    public RedisLock(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Attempt to acquire redis distribution lock.
     * <p>
     * If can't acquire lock, loop until timeout or acquired a lock.
     * Every loop has 1000 millis duration.
     *
     * @param lockName custom lock name for identification.
     *
     * @return lock identifier if successfully acquired a lock otherwise return null.
     */
    @NotNull
    public String acquireDistributionLock(@NotNull String lockName) throws AcquireLockException {
        return acquireDistributionLock(lockName, lockExpireSeconds);
    }

    /**
     * Acquire distribution lock with expire time.
     * <p>
     * If the thread acquire a lock without expire time, and the thread crashed, the lock can't release any more.
     *
     * @param lockName lock name for
     * @param seconds lock expire seconds
     * @return
     */
    @NotNull
    public String acquireDistributionLock(@NotNull String lockName, int seconds) throws AcquireLockException {
        String identifier = UUID.randomUUID().toString();
        final long endTime = System.currentTimeMillis() + timeoutMillis;
        while (StringUtils.isNotBlank(identifier) && System.currentTimeMillis() < endTime) {
            boolean acquired = redisService.setnx(buildKey(lockName), identifier) == 1;
            if (acquired) {
                redisService.expire(buildKey(lockName), seconds);
                return identifier;
            } else {
                try {
                    Thread.sleep(acquireDurationMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        throw RedisException.acquireDistributionLockException();
    }

    /**
     * Acquire semaphore distribution lock with limit and timeout.
     *
     * @param lockName lock name
     * @param limit the thread amount that the lock can be hold
     * @param seconds lock timeout seconds
     * @return identifier
     */
    @NotNull
    public String acquireSemaphoreDistributionLock(@NotNull String lockName, int limit, int seconds)
            throws AcquireLockException {
        String identifier = UUID.randomUUID().toString();
        // clear expired keys
        redisService.zremrangeByScore(buildKey(lockName), 0, System.currentTimeMillis() / 1000 - seconds);
        // add identifier to sorted set
        redisService.zadd(buildKey(lockName), System.currentTimeMillis() / 1000, identifier);
        // get identifier rank
        Long rank = redisService.zrank(buildKey(lockName), identifier);
        // rank is smaller than limit
        if (rank != null && rank.intValue() < limit) {
            return identifier;
        }
        // remove identifier from sorted set
        redisService.zrem(buildKey(lockName), identifier);
        throw RedisException.acquireDistributionLockException();
    }

    /**
     * Acquire fair semphore distribution lock, can avoid competition.
     *
     * @param lockName
     * @param limit
     * @param seconds
     * @return
     */
    @NotNull
    public String acquireFairSemphoreDistributionLock(@NotNull String lockName, int limit, int seconds)
            throws AcquireLockException {
        // acquire lock failed.
        String identifier;
        try {
            identifier = acquireDistributionLock(lockName, fairSemaphoreCompetitionTimeout);
        } catch (AcquireLockException ex) {
            identifier = null;
        }
        if (StringUtils.isNotBlank(identifier)) {
            try {
                return acquireFairSemaphoreLock(lockName, limit, seconds);
            } catch (AcquireLockException ex) {
                releaseLock(lockName, identifier);
            }
        }
        throw RedisException.acquireDistributionLockException();
    }

    /**
     * Acquire fair semaphore distribution lock.
     *
     * @param lockName
     * @param limit
     * @param seconds
     * @return
     * @throws AcquireLockException
     */
    @NotNull
    private String acquireFairSemaphoreLock(@NotNull String lockName, int limit, int seconds)
            throws AcquireLockException {
        String zsetName = buildLockSetKey(lockName);
        String zsetCounter = buildKey(lockName) + "_zset_counter";
        String identifier = UUID.randomUUID().toString();
        Transaction transaction = redisService.multi();
        // delete expired key from lockName sorted set
        redisService.zremrangeByScore(lockName, 0, System.currentTimeMillis() / 1000 - seconds);
        // delete related lock counter from sorted set
        ZParams params = new ZParams();
        params.weights(1, 0);
        transaction.zinterstore(zsetName, params, zsetName, lockName);
        // increase counter value
        transaction.incr(zsetCounter);
        List<Object> results = transaction.exec();
        int counter = ((Long) results.get(results.size() - 1)).intValue();

        // add to sorted set
        transaction = redisService.multi();
        transaction.zadd(buildKey(lockName), System.currentTimeMillis() / 1000, identifier);
        transaction.zadd(zsetName, counter, identifier);
        transaction.zrank(zsetName, identifier);
        // get rank
        results = transaction.exec();
        int rank = ((Long) results.get(results.size() - 1)).intValue();
        // can acquire lock
        if (rank < limit) {
            return identifier;
        }
        transaction = redisService.multi();
        transaction.zrem(buildKey(lockName), identifier);
        transaction.zrem(zsetName, identifier);
        transaction.exec();
        throw RedisException.acquireDistributionLockException();
    }

    /**
     * Refresh the expire time of the fair semaphore distribution lock.
     *
     * @param lockName
     * @param identifier
     * @return true if the lock expire time has been refreshed, false if the lock already expired and removed.
     */
    public boolean refreshFairSemaphone(@NotNull String lockName, @NotNull String identifier) {
        // the thread already lost the fair semaphore lock
        if (redisService.zadd(buildKey(lockName), System.currentTimeMillis() / 1000, identifier) == 1) {
            releaseFairSemaphoreLock(lockName, identifier);
            return false;
        }
        return true;
    }

    /**
     * Release a redis distribution lock.
     *
     * @param lockName the lock name need to be released.
     *
     * @return true if released successfully, otherwise return false.
     */
    public boolean releaseLock(@NotNull String lockName, @NotNull String identifier) {
        final long endTime = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < endTime) {
            try {
                redisService.watch(buildKey(lockName));
                // if the thread still hold lock
                if (identifier.equals(redisService.get(buildKey(lockName)))) {
                    Transaction transaction = redisService.multi();
                    redisService.del(buildKey(lockName));
                    transaction.exec();
                    return true;
                } else {
                    // the thread lost the lock
                    redisService.unwatch();
                    break;
                }
            } catch (JedisDataException ex) {
                // if the lock was modified by others, retry
                continue;
            }
        }
        return true;
    }

    /**
     * Release semaphore distribution lock.
     *
     * @param lockName
     * @param identifier
     * @return
     */
    public boolean releaseSemaphoreLock(@NotNull String lockName, @NotNull String identifier) {
        return redisService.zrem(buildKey(lockName), identifier) == 1;
    }

    /**
     * Release fair semaphore distribution lock.
     *
     * @param lockName
     * @param identifier
     * @return
     */
    public boolean releaseFairSemaphoreLock(@NotNull String lockName, @NotNull String identifier) {
        Transaction transaction = redisService.multi();
        transaction.zrem(buildKey(lockName), identifier);
        transaction.zrem(buildLockSetKey(lockName), identifier);
        transaction.exec();
        return true;
    }

    private String buildKey(@NotNull String lockName) {
        return KEY_REDIS_LOCK + "_" + lockName;
    }

    private String buildLockSetKey(@NotNull String lockName) {
        return buildKey(lockName) + "_zset";
    }
}
