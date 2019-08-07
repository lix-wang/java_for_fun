package com.xiao.framework.redis.jedis;

import com.xiao.framework.base.exception.LixException;
import com.xiao.framework.base.exception.LixStatusCode;

/**
 * Custom redis exception.
 *
 * @author lix wang
 */
public class RedisException {
    public static AcquireLockException acquireDistributionLockException() {
        return new AcquireLockException(LixException.builder()
                .errorCode("redis.acquire_distribution_failed")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Acquire distribution lock failed.").build());
    }

    public static class AcquireLockException extends LixException {
        public AcquireLockException(Throwable cause) {
            super(cause);
        }
    }
}
