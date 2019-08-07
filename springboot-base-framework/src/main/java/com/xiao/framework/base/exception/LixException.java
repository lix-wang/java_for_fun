package com.xiao.framework.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Custom exception.
 *
 * @author lix wang
 */
public class LixException extends Exception {
    @Getter
    @Setter
    private LixStatusCode statusCode;
    @Getter
    @Setter
    private String errorCode;
    @Getter
    @Setter
    private String message;

    public LixException() {
        super();
    }

    public LixException(Throwable cause) {
        super(cause);
    }

    private LixException(LixExceptionBuilder<LixException> builder) {
        super();
        this.statusCode = builder.statusCode();
        this.errorCode = builder.errorCode();
        this.message = builder.message();
    }

    public static LixExceptionBuilder<LixException> builder() {
        return new LixExceptionBuilder<LixException>() {
            @Override
            public LixException build() {
                return new LixException(this);
            }
        };
    }

    public LixRuntimeException toRuntimeException() {
        return LixRuntimeException.builder()
                .errorCode(this.errorCode)
                .message(this.message)
                .statusCode(this.statusCode).build();
    }
}
