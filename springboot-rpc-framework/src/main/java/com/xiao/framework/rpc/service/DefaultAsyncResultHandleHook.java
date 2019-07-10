package com.xiao.framework.rpc.service;

import com.xiao.framework.rpc.model.AbstractAsyncResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default hook to handle rpc failure.
 *
 * @author lix wang
 */
public class DefaultAsyncResultHandleHook implements BaseAsyncResultHandleHook {
    private static final Logger logger = LogManager.getLogger(DefaultAsyncResultHandleHook.class);
    private static final int DEFAULT_MAX_RETRY_TIMES = 3;
    private static final int DEFAULT_SLEEP_MILLIONS = 1000;

    @Override
    public <T> T onGetException(Exception e, AbstractAsyncResult<T> asyncResult) {
        T result = null;
        logger.error("AsyncResult execute get() failed: " + e, e);
        int count = 0;
        while (count++ < DEFAULT_MAX_RETRY_TIMES) {
            try {
                result = asyncResult.handleResult();
                break;
            } catch (Exception ex) {
                logger.error("AsyncResult get() retry " + count + " failed: " + ex, ex);
                try {
                    Thread.sleep(DEFAULT_SLEEP_MILLIONS * count);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return result;
    }
}
