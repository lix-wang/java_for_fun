package com.xiao.framework.queue;

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
    private String queueName;
    private Class<? extends RedisQueueTask> taskClass;
    // this field support custom, if null redis queue will generate automatically
    private String identifier;
}
