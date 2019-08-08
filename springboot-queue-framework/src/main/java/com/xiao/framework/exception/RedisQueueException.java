package com.xiao.framework.exception;

import com.xiao.framework.base.exception.LixException;
import com.xiao.framework.base.exception.LixStatusCode;

/**
 * Custom redis queue exception.
 *
 * @author lix wang
 */
public class RedisQueueException {
    public static QueueStateException queueStateException() {
        return new QueueStateException(LixException.builder()
                .errorCode("queue.queue_state_is_closed")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Redis queue state is closed.").build());
    }

    public static class QueueStateException extends LixException {
        public QueueStateException(Throwable cause) {
            super(cause);
        }
    }
}
