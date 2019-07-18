package com.xiao.framework.rpc.http;

import okhttp3.Response;

import javax.validation.constraints.NotNull;

import java.util.concurrent.Future;

/**
 * Http call interface for proxy.
 *
 * @author lix wang
 */
public interface HttpCall {
    Future<Response> asyncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper);
}
