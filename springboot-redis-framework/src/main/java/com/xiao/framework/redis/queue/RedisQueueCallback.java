package com.xiao.framework.redis.queue;

/**
 * Redis queue callback.
 *
 * @author lix wang
 */
@FunctionalInterface
public interface RedisQueueCallback {
    <D extends Object, E> D execute(E... params);
}
