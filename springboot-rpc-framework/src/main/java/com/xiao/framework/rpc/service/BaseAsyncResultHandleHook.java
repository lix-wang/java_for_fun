package com.xiao.framework.rpc.service;

import com.xiao.framework.rpc.model.AbstractAsyncResult;

/**
 * Base interface of async rpc hook.
 *
 * @author lix wang
 */
public interface BaseAsyncResultHandleHook {
    <T> T onGetException(Exception e, AbstractAsyncResult<T> asyncResult);
}
