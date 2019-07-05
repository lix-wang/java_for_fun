package com.xiao.framework.rpc.service;

import com.xiao.framework.rpc.model.AbstractAsyncResult;
import lombok.extern.log4j.Log4j2;

/**
 * Default hook to handle rpc failure.
 *
 * @author lix wang
 */
@Log4j2
public class DefaultAsyncResultHandleHook implements BaseAsyncResultHandleHook {
    private static final int DEFAULT_MAX_RETRY_TIMES = 3;
    private static final int DEFAULT_SLEEP_MILLIONS = 1000;

    @Override
    public <T> T onGetException(Exception e, AbstractAsyncResult<T> asyncResult) {
        T result = null;
        log.error("AsyncResult execute get() failed: " + e, e);
        int count = 0;
        while (count++ < DEFAULT_MAX_RETRY_TIMES) {
            try {
                result = asyncResult.handleResult();
                break;
            } catch (Exception ex) {
                log.error("AsyncResult get() retry " + count + " failed: " + ex, ex);
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
