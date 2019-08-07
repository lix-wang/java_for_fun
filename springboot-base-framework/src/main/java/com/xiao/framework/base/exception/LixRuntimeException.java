package com.xiao.framework.base.exception;

import lombok.Getter;

/**
 * Custom runtime exception.
 *
 * @author lix wang
 */
public class LixRuntimeException extends RuntimeException {
    @Getter
    private LixStatusCode statusCode;
    @Getter
    private String errorCode;
    @Getter
    private String message;

    public static LixExceptionBuilder<LixRuntimeException> builder() {
        return new LixExceptionBuilder<LixRuntimeException>() {
            @Override
            public LixRuntimeException build() {
                return new LixRuntimeException(this);
            }
        };
    }

    public LixRuntimeException() {
        super();
    }

    private LixRuntimeException(LixExceptionBuilder<LixRuntimeException> builder) {
        super();
        this.errorCode = builder.errorCode();
        this.statusCode = builder.statusCode();
        this.message = builder.message();
    }
}
