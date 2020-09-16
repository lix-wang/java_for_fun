package com.xiao.framework.rpc.proxy;

import lombok.Data;

import java.lang.reflect.InvocationHandler;

/**
 * Abstract invocation handler.
 *
 * @author lix wang
 */
@Data
public abstract class AbstractInvocationHandler<T> implements InvocationHandler {
    private final Class<T> targetClass;
    private final T targetObject;

    protected AbstractInvocationHandler(Class<T> targetClass, T targetObject) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
    }
}
