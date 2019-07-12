package com.xiao.framework.rpc.okhttp;

import com.sun.tools.javac.util.Assert;
import com.xiao.framework.rpc.thread.ThreadPoolHelper;
import com.xiao.framework.rpc.util.JsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Custom sync and async http callAsync.
 *
 * @author lix wang
 */
public class OkHttpCall {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final Logger logger = LogManager.getLogger(OkHttpCall.class);

    /**
     * make sync callAsync response string
     */
    public static String syncCall(@NotNull OkHttpExecutionWrapper wrapper) throws IOException {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return getResponse(getCall(wrapper.okHttpClient(), wrapper.request())).body().string();
    }

    /**
     * make sync callAsync response target Class type. Only support application/json response body.
     */
    public static <T> T syncCall(@NotNull OkHttpExecutionWrapper wrapper, Class<T> clazz)
            throws IOException {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        ResponseBody responseBody = getResponse(getCall(wrapper.okHttpClient(), wrapper.request())).body();
        Assert.check(JSON_MEDIA_TYPE == responseBody.contentType(),
                "This method can only handle application/json response body.");
        return JsonUtil.convertToObject(responseBody.byteStream(), clazz);
    }

    public static Response syncCallResponse(@NotNull OkHttpExecutionWrapper wrapper) throws IOException {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return getResponse(getCall(wrapper.okHttpClient(), wrapper.request()));
    }

    /**
     * make async callAsync response String.
     */
    public static Future<String> asyncCall(@NotNull OkHttpExecutionWrapper wrapper, @NotNull Callback callback) {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return ThreadPoolHelper.getPool().submit(
                () -> syncCallWithCallback(wrapper.okHttpClient(), wrapper.request(), callback).body().string());
    }

    /**
     * make async callAsync response target Class type. Only support application/json response body.
     */
    public static <T> Future<T> asyncCall(@NotNull OkHttpExecutionWrapper wrapper, @NotNull Class<T> clazz,
            @NotNull Callback callback) {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return ThreadPoolHelper.getPool().submit(() -> getSyncCallResult(
                syncCallWithCallback(wrapper.okHttpClient(), wrapper.request(), callback), clazz));
    }

    public static Future<Response> asyncCallResponse(@NotNull OkHttpExecutionWrapper wrapper,
            @NotNull Callback callback) {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return ThreadPoolHelper.getPool().submit(
                () -> syncCallWithCallback(wrapper.okHttpClient(), wrapper.request(), callback));
    }

    private static <T> T getSyncCallResult(@NotNull Response response, @NotNull Class<T> clazz) {
        Assert.check(JSON_MEDIA_TYPE == response.body().contentType(),
                "This method can only handle application/json response body.");
        return JsonUtil.convertToObject(response.body().byteStream(), clazz);
    }

    private static Response syncCallWithCallback(@NotNull OkHttpClient client, @NotNull Request request,
            @NotNull Callback callback) {
        Response response = null;
        Call call = null;
        try {
            call = getCall(client, request);
            response = getResponse(call);
            callback.onResponse(call, response);
        } catch (IOException e) {
            logger.error("Async http callAsync failed " + e.getMessage(), e);
            callback.onFailure(call, e);
        }
        return response;
    }

    private static Response getResponse(@NotNull Call call) throws IOException {
        return call.execute();
    }

    private static Call getCall(@NotNull OkHttpClient client, @NotNull Request request) {
        return client.newCall(request);
    }
}
