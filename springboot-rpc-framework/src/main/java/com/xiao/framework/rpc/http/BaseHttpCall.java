package com.xiao.framework.rpc.http;

import com.xiao.framework.rpc.okhttp.DefaultOkHttpCall;
import com.xiao.framework.rpc.proxy.AsyncHttpCallProxy;
import com.xiao.framework.rpc.util.JsonUtil;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.Future;

/**
 * Http callAsync.
 *
 * @author lix wang
 */
public class BaseHttpCall implements HttpCall {
    public static final ThreadLocal<Callback> ASYNC_HTTP_CALLBACK = new ThreadLocal<>();

    /**
     * async all callAsync return Future<Response>
     * I won't use it directly, I will use {@link AsyncHttpCallProxy} instead.
     */
    @Override
    public Future<Response> asyncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper) {
        return DefaultOkHttpCall.asyncCallResponse(getRequest(wrapper), ASYNC_HTTP_CALLBACK.get());
    }

    /**
     * handle all content-type sync okhttp callAsync return response
     * Now, I make this method private, because it means useless for me currently.
     * I write this method only want to show how to handle sync call.
     */
    private Response syncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper) throws IOException {
        return DefaultOkHttpCall.syncCallResponse(getRequest(wrapper));
    }

    private static Request getRequest(@NotNull HttpRequestWrapper requestWrapper) {
        switch (requestWrapper.httpType()) {
            case GET:
                return constructGetRequest(requestWrapper.url(), requestWrapper.parameters(), requestWrapper.headers());
            case POST:
                return constructPostRequest(requestWrapper.url(), requestWrapper.parameters(),
                        requestWrapper.headers());
            default:
                throw new RuntimeException("not_supported_http_method");
        }
    }

    private static Request constructGetRequest(@NotNull String url, LinkedHashMap<String, String> parameters,
            LinkedHashMap<String, String> headers) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        if (MapUtils.isNotEmpty(parameters)) {
            parameters.entrySet().forEach(entry -> httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue()));
        }
        Request request = new Request.Builder()
                .url(httpUrlBuilder.build().toString()).build();
        setRequestHeaders(request, headers);
        return request;
    }

    private static Request constructPostRequest(@NotNull String url, @NotNull LinkedHashMap<String, String> parameters,
            @NotNull LinkedHashMap<String, String> headers) {
        RequestBody requestBody = null;
        if (MapUtils.isNotEmpty(parameters)) {
            requestBody = RequestBody.create(JsonUtil.serialize(parameters),
                    MediaType.parse(HttpContentType.APPLICATION_JSON.getContentType()));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        setRequestHeaders(request, headers);
        return request;
    }

    private static void setRequestHeaders(@NotNull Request request, @NotNull LinkedHashMap<String, String> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            headers.entrySet().forEach(entry -> request.newBuilder().header(entry.getKey(), entry.getValue()));
        }
    }
}
