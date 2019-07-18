package com.xiao.framework.biz.exception;

import lombok.Getter;

/**
 * Custom runtime exception.
 *
 * @author lix wang
 */
public class LixRuntimeException extends RuntimeException {
    @Getter
    private int statusCode;
    @Getter
    private String errorCode;
    @Getter
    private String message;

    public static Builder builder() {
        return new Builder();
    }

    private LixRuntimeException(Builder builder) {
        this.errorCode = builder.errorCode;
        this.statusCode = builder.statusCode;
        this.message = builder.message;
    }

    public static final class Builder {
        private int statusCode;
        private String errorCode;
        private String message;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public LixRuntimeException build() {
            return new LixRuntimeException(this);
        }
    }
}
