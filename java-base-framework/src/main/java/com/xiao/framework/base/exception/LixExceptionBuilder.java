package com.xiao.framework.base.exception;

/**
 *
 * @author lix wang
 */
public abstract class LixExceptionBuilder<T> {
    private LixStatusCode statusCode;
    private String errorCode;
    private String message;

    public LixExceptionBuilder<T> statusCode(LixStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public LixExceptionBuilder<T> errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public LixExceptionBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public LixStatusCode statusCode() {
        return this.statusCode;
    }

    public String errorCode() {
        return this.errorCode;
    }

    public String message() {
        return this.message;
    }

    public abstract T build();
}
