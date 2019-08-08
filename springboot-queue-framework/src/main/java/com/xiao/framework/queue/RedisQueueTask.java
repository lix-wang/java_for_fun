package com.xiao.framework.queue;

/**
 * Redis queue callback.
 *
 * @author lix wang
 */
@FunctionalInterface
public interface RedisQueueTask {
    <D extends Object, E> D execute(E... params);
}
