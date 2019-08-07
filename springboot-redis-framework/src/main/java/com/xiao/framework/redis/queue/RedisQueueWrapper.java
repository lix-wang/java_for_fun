package com.xiao.framework.redis.queue;

import lombok.Builder;
import lombok.Data;

/**
 * Wrapper of redis queue.
 *
 * @author lix wang
 */
@Data
@Builder
public class RedisQueueWrapper {
    private String identifier;
    private String queueName;
    private RedisQueueCallback callback;
}
