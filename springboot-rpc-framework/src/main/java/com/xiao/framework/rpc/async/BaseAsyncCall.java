package com.xiao.framework.rpc.async;

import com.xiao.framework.rpc.model.AsyncResult;
import com.xiao.framework.rpc.model.BaseAsyncResult;
import com.xiao.framework.rpc.thread.ThreadPoolHelper;

import javax.validation.constraints.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Async async callAsync async.
 *
 * @author lix wang
 */
public class BaseAsyncCall {
    /**
     * Start sync call async method.
     */
    public static <T> AsyncResult<T> callAsync(@NotNull Callable<Future<T>> callable, BaseAsyncResultHandleHook hook)
            throws Exception {
        AsyncResult<T> asyncResult = constructAsyncResult(hook);
        DefaultAsyncFactory.setAsyncContext(asyncResult);
        asyncResult.setResultFuture(callable.call());
        DefaultAsyncFactory.setAsyncContext(null);
        return asyncResult;
    }

    /**
     * Start async call sync method.
     */
    public static <T> BaseAsyncResult<T> asyncCall(@NotNull Callable<T> callable, BaseAsyncResultHandleHook hook) {
        BaseAsyncResult<T> baseAsyncResult = new BaseAsyncResult<>(hook);
        Future<T> futureResult = ThreadPoolHelper.getPool().submit(callable);
        baseAsyncResult.setResultFuture(futureResult);
        return baseAsyncResult;
    }

    private static <T> AsyncResult<T> constructAsyncResult(BaseAsyncResultHandleHook hook) {
        AsyncResult<T> asyncResult = new AsyncResult<>(hook);
        asyncResult.setStartTime(System.currentTimeMillis());
        return asyncResult;
    }
}
