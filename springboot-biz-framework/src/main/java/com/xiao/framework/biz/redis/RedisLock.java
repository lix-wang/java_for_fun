package com.xiao.framework.biz.redis;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Distribution lock implements by redis.
 *
 * @author lix wang
 */
public class RedisLock {
    private static final String KEY_REDIS_LOCK = "REDIS_LOCK";

    @Setter
    private int defaultLockExpireSeconds = 60;
    @Setter
    private long defaultAcquireDurationMillis = 1000;
    @Getter
    private long defaultTimeoutMillis = 5000;

    private final RedisService redisService;

    public RedisLock(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Attempt to acquire redis lock.
     * <p>
     * If can't acquire lock, loop until timeout or acquired a lock.
     * Every loop has 1000 millis duration.
     *
     * @param lockName custom lock name for identification.
     *
     * @return lock identifier if successfully acquired a lock otherwise return null.
     */
    public String acquireLock(@NotNull String lockName) {
        return acquireLock(lockName, defaultLockExpireSeconds);
    }

    /**
     * Acquire lock with expire time.
     * <p>
     * If the thread acquire a lock without expire time, and the thread crashed, the lock can't release any more.
     *
     * @param lockName lock name for
     * @param seconds lock expire seconds
     * @return
     */
    public String acquireLock(@NotNull String lockName, int seconds) {
        String result = null;
        String identifier = UUID.randomUUID().toString();
        final long endTime = System.currentTimeMillis() + defaultTimeoutMillis;
        while (StringUtils.isNotBlank(identifier) && System.currentTimeMillis() < endTime) {
            boolean acquired = redisService.setnx(buildKey(lockName), identifier) == 1;
            if (acquired) {
                redisService.expire(buildKey(lockName), seconds);
                result = identifier;
            } else {
                try {
                    Thread.sleep(defaultAcquireDurationMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Acquire lock with limit and timeout.
     *
     * @param lockName lock name
     * @param limit the thread amount that the lock can be acquired to
     * @param seconds lock timeout seconds
     * @return identifier
     */
    public String acquireLock(@NotNull String lockName, int limit, int seconds) {
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
        return null;
    }

    /**
     * Release a redis distribution lock.
     *
     * @param lockName the lock name need to be released.
     *
     * @return true if released successfully, otherwise return false.
     */
    public boolean releaseLock(@NotNull String lockName, @NotNull String identifier) {
        final long endTime = System.currentTimeMillis() + defaultTimeoutMillis;
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
                    redisService.unwatch();
                    break;
                }
            } catch (JedisDataException ex) {
                // if the lock was modified by others, retry
                continue;
            }
        }
        return false;
    }

    private String buildKey(@NotNull String lockName) {
        return KEY_REDIS_LOCK + "_" + lockName;
    }
}
