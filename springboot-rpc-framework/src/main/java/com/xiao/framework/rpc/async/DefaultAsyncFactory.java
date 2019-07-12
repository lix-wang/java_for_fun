package com.xiao.framework.rpc.async;

import com.xiao.framework.rpc.model.AsyncResult;

/**
 * ExecutorFactory to get executor;
 *
 * @author lix wang
 */
public class DefaultAsyncFactory {
    private static final ThreadLocal<AsyncResult> ASYNC_CONTEXT = new ThreadLocal<>();
    private static final BaseAsyncResultHandleHook DEFAULT_HOOK = new DefaultAsyncResultHandleHook();

    public static BaseAsyncResultHandleHook getDefaultHook() {
        return DEFAULT_HOOK;
    }

    public static <T> AsyncResult<T> getAsyncContext() {
        return ASYNC_CONTEXT.get();
    }

    public static <T> void setAsyncContext(AsyncResult<T> asyncResult) {
        ASYNC_CONTEXT.set(asyncResult);
    }
}
