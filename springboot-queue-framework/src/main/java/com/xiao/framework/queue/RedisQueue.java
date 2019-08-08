package com.xiao.framework.queue;

import com.xiao.framework.base.utils.JsonUtil;
import com.xiao.framework.exception.RedisQueueException;
import com.xiao.framework.exception.RedisQueueException.QueueStateException;
import com.xiao.framework.redis.exception.RedisException.AcquireLockException;
import com.xiao.framework.redis.jedis.RedisLock;
import com.xiao.framework.redis.jedis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Tuple;

import javax.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * Custom redis task queue.
 *
 * @author lix wang
 */
public class RedisQueue {
    private static final String KEY_REDIS_QUEUE = "REDIS_QUEUE";
    private static final String KEY_QUEUE_READY = "READY";
    private static final String KEY_QUEUE_WAIT = "WAIT";

    private boolean queueRunningFlag = false;
    private long queuePollBeatMillis = 20;

    private final RedisService redisService;
    private final RedisLock redisLock;

    public RedisQueue(RedisService redisService) {
        this.redisService = redisService;
        this.redisLock = new RedisLock(redisService);
    }

    /**
     * Submit task to redis queue.
     *
     * @param wrapper params that redis queue required.
     * @param delayMillis task execute delay millis.
     * @return task identifier.
     */
    public String submitTask(@NotNull RedisQueueWrapper wrapper, long delayMillis) throws QueueStateException {
        if (!this.queueRunningFlag) {
            throw RedisQueueException.queueStateException();
        }
        if (StringUtils.isBlank(wrapper.getIdentifier())) {
            wrapper.setIdentifier(UUID.randomUUID().toString());
        }
        if (delayMillis > 0) {
            redisService.zadd(getWaitQueueName(), System.currentTimeMillis() + delayMillis,
                    JsonUtil.serialize(wrapper));
        } else {
            redisService.rpush(getReadyQueueName(wrapper.getQueueName()), JsonUtil.serialize(wrapper));
        }
        return wrapper.getIdentifier();
    }

    /**
     * Loop to poll task from wait queue to ready queue.
     */
    public void pollTask() {
        Tuple item = null;
        while (this.queueRunningFlag || (item = getFirstFromWait()) != null) {
            // null or not ready for execute.
            if (item != null && item.getScore() > System.currentTimeMillis()) {
                try {
                    Thread.sleep(this.queuePollBeatMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            RedisQueueWrapper taskWrapper = JsonUtil.deserialize(item.getElement(), RedisQueueWrapper.class);
            // use redis lock
            String lockIdentifier;
            try {
                lockIdentifier = redisLock.acquireDistributionLock(taskWrapper.getIdentifier(), 5);
            } catch (AcquireLockException e) {
                // acquire lock failed, continue
                continue;
            }
            // move task from wait queue to ready queue
            if (redisService.zrem(getWaitQueueName(), item.getElement()) == 1) {
                redisService.rpush(getReadyQueueName(taskWrapper.getQueueName()), item.getElement());
            }
            // release lock
            redisLock.releaseLock(taskWrapper.getIdentifier(), lockIdentifier);
        }
    }

    /**
     * Stop the queue.
     * <p>
     * if stopped the queue, then the queue will finish all tasks first and then close.
     * if the queue is stopped, then the queue can't submit any task right now.
     */
    public void stopQueue() {
        this.queueRunningFlag = false;
    }

    /**
     * Define duration of poll task.
     *
     * @param millis
     */
    public void setQueuePollBeatMillis(long millis) {
        this.queuePollBeatMillis = millis;
    }

    /**
     * Get first task from wait queue.
     * @return
     */
    private Tuple getFirstFromWait() {
        // get first item
        Set<Tuple> items = redisService.zrangeWithScores(getWaitQueueName(), 0, 0);
        return CollectionUtils.isNotEmpty(items) ? items.iterator().next() : null;
    }

    private String buildName(@NotNull String name1, @NotNull String name2) {
        return name1 + ":" + name2;
    }

    private String getReadyQueueName(@NotNull String queueName) {
        return buildName(buildName(KEY_REDIS_QUEUE, KEY_QUEUE_READY), queueName);
    }

    private String getWaitQueueName() {
        return buildName(KEY_REDIS_QUEUE, KEY_QUEUE_WAIT);
    }
}
