package com.xiao.framework.rpc.okhttp;

import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Default http call.
 *
 * @author lix wang
 */
public class DefaultHttpCall {
    public static Response syncCallResponse(@NotNull Request request) throws IOException {
        HttpExecutionWrapper wrapper = HttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultConnectionPool.getDefaultClient()).build();
        return OkHttpCall.syncCallResponse(wrapper);
    }

    public static Future<Response> asyncCallResponse(@NotNull Request request, @NotNull Callback callback) {
        HttpExecutionWrapper wrapper = HttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultConnectionPool.getDefaultClient()).build();
        return OkHttpCall.asyncCallResponse(wrapper, callback);
    }
}
