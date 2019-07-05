package com.xiao.framework.rpc.model;

import com.xiao.framework.rpc.service.BaseAsyncResultHandleHook;
import lombok.Setter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Abstract async result.
 *
 * @author lix wang
 */
public abstract class AbstractAsyncResult<T> {
    @Setter
    public Future<T> resultFuture;

    private final BaseAsyncResultHandleHook baseAsyncResultHandleHook;

    public AbstractAsyncResult(BaseAsyncResultHandleHook baseAsyncResultHandleHook) {
        this.baseAsyncResultHandleHook = baseAsyncResultHandleHook;
    }

    /**
     * Get resultFuture use sync.
     */
    public T get() {
        T result = null;
        try {
            result = handleResult();
        } catch (Exception e) {
            if (baseAsyncResultHandleHook != null) {
                return baseAsyncResultHandleHook.onGetException(e, this);
            }
        }
        return result;
    }

    public abstract T handleResult() throws ExecutionException, InterruptedException;
}
