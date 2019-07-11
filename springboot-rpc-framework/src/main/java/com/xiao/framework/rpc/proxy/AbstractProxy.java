package com.xiao.framework.rpc.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * Base proxy object.
 *
 * @author lix wang
 */
public abstract class AbstractProxy<T> implements InvocationHandler {
    public final Class<T> realClazz;
    public final T realObject;

    public AbstractProxy(Class<T> realClazz, T realObject) {
        this.realClazz = realClazz;
        this.realObject = realObject;
    }
}
