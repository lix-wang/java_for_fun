package com.xiao.framework.redis.exception;

import com.xiao.framework.base.exception.LixException;
import com.xiao.framework.base.exception.LixStatusCode;

/**
 * Custom Jedis Exception.
 *
 * @author lix wang
 */
public class JedisCustomException {
    public static ConnectionException connectionException() {
        return new ConnectionException(LixException.builder()
                .errorCode("jedis.connection_failed")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Connection exception when get jedis.").build());
    }

    public static ExhaustedPoolException exhaustedPoolException() {
        return new ExhaustedPoolException(LixException.builder()
                .errorCode("jedis.exhausted_pool")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Exhausted exception when get jedis.").build());
    }

    public static ValidationException validationException() {
        return new ValidationException(LixException.builder()
                .errorCode("jedis.validation_failed")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Active or validate exception when get jedis.").build());
    }

    public static AcquireResourceException acquireResourceException() {
        return new AcquireResourceException(LixException.builder()
                .errorCode("jedis.acquire_resource_failed")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Acquire resource from jedis pool failed.").build());
    }

    public static NoValidJedis noValidJedis() {
        return new NoValidJedis(LixException.builder()
                .errorCode("jedis.no_valid_jedis")
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("No valid jedis").build());
    }

    public static class ConnectionException extends LixException {
        public ConnectionException(Throwable cause) {
            super(cause);
        }
    }

    public static class ExhaustedPoolException extends LixException {
        public ExhaustedPoolException(Throwable cause) {
            super(cause);
        }
    }

    public static class ValidationException extends LixException {
        public ValidationException(Throwable cause) {
            super(cause);
        }
    }

    public static class AcquireResourceException extends LixException {
        public AcquireResourceException(Throwable cause) {
            super(cause);
        }
    }

    public static class NoValidJedis extends LixException {
        public NoValidJedis(Throwable cause) {
            super(cause);
        }
    }
}
