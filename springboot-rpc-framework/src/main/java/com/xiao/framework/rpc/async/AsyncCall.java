package com.xiao.framework.rpc.async;

import com.xiao.framework.rpc.proxy.AbstractProxy;
import com.xiao.framework.rpc.model.AbstractAsyncResult;

import javax.validation.constraints.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Default async rpc call.
 *
 * @author lix wang
 */
public class AsyncCall {
    /**
     * Call async Callable, the Callable should come from {@link AbstractProxy}
     */
    public static <T> AbstractAsyncResult<T> callAsync(@NotNull Callable<Future<T>> callable) throws Exception {
        return BaseAsyncCall.call(callable, DefaultAsyncFactory.getDefaultHook());
    }

    /**
     * Async call common sync Callable.
     */
    public static <T> AbstractAsyncResult<T> asyncCall(@NotNull Callable<T> callable) throws Exception {
        return BaseAsyncCall.asyncCall(callable, DefaultAsyncFactory.getDefaultHook());
    }
}
