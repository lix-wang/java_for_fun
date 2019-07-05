package com.xiao.framework.rpc.model;

/**
 * Async result for call async callable.
 *
 * @author lix wang
 */

import com.xiao.framework.rpc.service.BaseAsyncResultHandleHook;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutionException;

/**
 * Result of Async Callable.
 *
 * @author lix wang
 */
@Log4j2
public class AsyncResult<T> extends AbstractAsyncResult<T> {
    @Setter
    private long startTime;
    @Setter
    private long endTime;

    public AsyncResult(BaseAsyncResultHandleHook baseAsyncResultHandleHook) {
        super(baseAsyncResultHandleHook);
    }

    /**
     * Handle rpc resultFuture.
     */
    @Override
    public T handleResult() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        T result = resultFuture.get();
        log.info("Get async result consume: " + (System.currentTimeMillis() - start) + " ms"
                + " total execution consume: " + (endTime - startTime) + " ms");
        return result;
    }
}