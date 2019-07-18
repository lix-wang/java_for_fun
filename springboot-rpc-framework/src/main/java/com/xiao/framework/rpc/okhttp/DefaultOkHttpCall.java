package com.xiao.framework.rpc.okhttp;

import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Default http callAsync.
 *
 * @author lix wang
 */
public class DefaultOkHttpCall {
    public static Response syncCallResponse(@NotNull Request request) throws IOException {
        OkHttpExecutionWrapper wrapper = OkHttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultOkHttpConnectionPool.getDefaultClient()).build();
        return OkHttpCall.syncCallResponse(wrapper);
    }

    public static Future<Response> asyncCallResponse(@NotNull Request request, @NotNull Callback callback) {
        OkHttpExecutionWrapper wrapper = OkHttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultOkHttpConnectionPool.getDefaultClient()).build();
        return OkHttpCall.asyncCallResponse(wrapper, callback);
    }
}
