package com.xiao.framework.rpc.model;

/**
 * Async result for call async callable.
 *
 * @author lix wang
 */

import com.xiao.framework.rpc.service.BaseAsyncResultHandleHook;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * Result of Async Callable.
 *
 * @author lix wang
 */
public class AsyncResult<T> extends AbstractAsyncResult<T> {
    private static final Logger logger = LogManager.getLogger(AsyncResult.class);

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
        logger.info("Get async result consume: " + (System.currentTimeMillis() - start) + " ms"
                + " total execution consume: " + (endTime - startTime) + " ms");
        return result;
    }
}