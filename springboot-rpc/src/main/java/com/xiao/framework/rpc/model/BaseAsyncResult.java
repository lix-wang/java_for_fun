package com.xiao.framework.rpc.model;

import com.xiao.framework.rpc.async.BaseAsyncResultHandleHook;

import java.util.concurrent.ExecutionException;

/**
 * Async result for async callAsync callable.
 *
 * @author lix wang
 */
public class BaseAsyncResult<T> extends AbstractAsyncResult<T> {
    public BaseAsyncResult(BaseAsyncResultHandleHook baseAsyncResultHandleHook) {
        super(baseAsyncResultHandleHook);
    }

    @Override
    public T handleResult() throws ExecutionException, InterruptedException {
        return resultFuture.get();
    }
}
