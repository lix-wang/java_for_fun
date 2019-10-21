package com.xiao.framework.base.exception;

/**
 * Custom exception.
 *
 * @author lix wang
 */
public class LixException extends Exception {
    private LixStatusCode statusCode;
    private String errorCode;
    private String message;
    private Throwable cause;

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
        this.cause = builder.cause();
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
                .statusCode(this.statusCode)
                .cause(this.cause).build();
    }
}
