package com.xiao.framework.rpc.proxy;

import com.xiao.framework.rpc.model.AsyncResult;
import com.xiao.framework.rpc.async.DefaultAsyncFactory;

import java.lang.reflect.Method;

/**
 * Base proxy object.
 *
 * @author lix wang
 */
public class BaseAsyncProxy<T> extends AbstractProxy<T> {
    public BaseAsyncProxy(Class<T> realClazz, T realObject) {
        super(realClazz, realObject);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        AsyncResult asyncResult = DefaultAsyncFactory.getAsyncContext();
        if (asyncResult != null) {
            if (realObject instanceof AbstractRpc) {
                AbstractRpc abstractRpc = (AbstractRpc) realObject;
                abstractRpc.execute();
            }
        }
        return null;
    }
}
