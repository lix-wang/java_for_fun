package com.xiao.framework.rpc.service;

import com.xiao.framework.rpc.model.AsyncResult;
import com.xiao.framework.rpc.thread.ThreadPoolHelper;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * ExecutorFactory to get executor;
 *
 * @author lix wang
 */
public class DefaultAsyncFactory {
    private static ThreadLocal<AsyncResult> ASYNC_CONTEXT = new ThreadLocal<>();
    private static final ThreadPoolExecutor DEFAULT_EXECUTOR = ThreadPoolHelper.getPool();
    private static final BaseAsyncResultHandleHook DEFAULT_HOOK = new DefaultAsyncResultHandleHook();

    public static ThreadPoolExecutor getDefaultExecutor() {
        return DEFAULT_EXECUTOR;
    }

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
