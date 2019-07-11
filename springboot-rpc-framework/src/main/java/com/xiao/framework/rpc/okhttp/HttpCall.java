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
 * Custom sync and async http call.
 *
 * @author lix wang
 */
public class HttpCall {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final Logger logger = LogManager.getLogger(HttpCall.class);

    /**
     * make sync call response string
     */
    public static String syncCall(@NotNull HttpRequestWrapper wrapper) throws IOException {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return getResponse(getCall(wrapper.okHttpClient(), wrapper.request())).body().string();
    }

    /**
     * make sync call response target Class type. Only support application/json response body.
     */
    public static <T> T syncCall(@NotNull HttpRequestWrapper wrapper, Class<T> clazz)
            throws IOException {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        ResponseBody responseBody = getResponse(getCall(wrapper.okHttpClient(), wrapper.request())).body();
        Assert.check(JSON_MEDIA_TYPE == responseBody.contentType(),
                "This method can only handle application/json response body.");
        return JsonUtil.convertToObject(responseBody.byteStream(), clazz);
    }

    /**
     * make async call response String.
     */
    public static Future<String> asyncCall(@NotNull HttpRequestWrapper wrapper, @NotNull Callback callback) {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return ThreadPoolHelper.getPool().submit(
                () -> syncCallWithCallback(wrapper.okHttpClient(), wrapper.request(), callback).body().string());
    }

    /**
     * make async call response target Class type. Only support application/json response body.
     */
    public static <T> Future<T> asyncCall(@NotNull HttpRequestWrapper wrapper, @NotNull Class<T> clazz,
            @NotNull Callback callback) {
        Assert.check(wrapper.okHttpClient() != null && wrapper.request() != null,
                "OkHttpClient and Request must not null.");
        return ThreadPoolHelper.getPool().submit(() -> getSyncCallResult(
                syncCallWithCallback(wrapper.okHttpClient(), wrapper.request(), callback), clazz));
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
            logger.error("Async http call failed " + e.getMessage(), e);
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
