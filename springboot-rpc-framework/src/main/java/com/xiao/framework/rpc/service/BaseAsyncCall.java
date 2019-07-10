package com.xiao.framework.rpc.service;

import com.xiao.framework.rpc.model.AsyncResult;
import com.xiao.framework.rpc.model.BaseAsyncResult;
import com.xiao.framework.rpc.thread.ThreadPoolHelper;

import javax.validation.constraints.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Async service call service.
 *
 * @author lix wang
 */
public class BaseAsyncCall {
    /**
     * Start call async method.
     */
    public static <T> AsyncResult<T> call(@NotNull Callable<T> callable, BaseAsyncResultHandleHook hook)
            throws Exception {
        AsyncResult<T> asyncResult = constructAsyncResult(hook);
        DefaultAsyncFactory.setAsyncContext(asyncResult);
        callable.call();
        DefaultAsyncFactory.setAsyncContext(null);
        return asyncResult;
    }

    /**
     * Start async call sync method.
     */
    public static <T> BaseAsyncResult<T> asyncCall(@NotNull Callable<T> callable, BaseAsyncResultHandleHook hook) {
        BaseAsyncResult<T> baseAsyncResult = new BaseAsyncResult<>(hook);
        Future<T> futreResult = ThreadPoolHelper.getPool().submit(callable);
        baseAsyncResult.setResultFuture(futreResult);
        return baseAsyncResult;
    }

    private static <T> AsyncResult<T> constructAsyncResult(BaseAsyncResultHandleHook hook) {
        AsyncResult<T> asyncResult = new AsyncResult<>(hook);
        asyncResult.setStartTime(System.currentTimeMillis());
        return asyncResult;
    }
}
