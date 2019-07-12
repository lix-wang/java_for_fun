package com.xiao.framework.rpc.http;

import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;

/**
 * Http call interface for proxy.
 *
 * @author lix wang
 */
public interface HttpCall {
    Future<Response> asyncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper);
}
