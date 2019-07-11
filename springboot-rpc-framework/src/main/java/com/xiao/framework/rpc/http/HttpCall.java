package com.xiao.framework.rpc.http;

import com.xiao.framework.rpc.okhttp.DefaultHttpCall;
import com.xiao.framework.rpc.util.JsonUtil;
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
 * Http call.
 *
 * @author lix wang
 */
public class HttpCall {
    // can handle all content-type response, return string
    public static String syncCall(@NotNull HttpRequestWrapper wrapper) throws IOException {
        switch (wrapper.httpClientType()) {
            case OK_HTTP:
                return DefaultHttpCall.syncCall(getRequest(wrapper));
            default:
                throw new RuntimeException("not_supported_http_client_type");
        }
    }

    // only can handle application/json response
    public static <T> T syncCall(@NotNull HttpRequestWrapper wrapper, @NotNull Class<T> clazz) throws IOException {
        switch (wrapper.httpClientType()) {
            case OK_HTTP:
                return DefaultHttpCall.syncCall(getRequest(wrapper), clazz);
            default:
                throw new RuntimeException("not_supported_http_client_type");
        }
    }

    // handle all content-type sync okhttp call return response
    public static Response syncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper) throws IOException {
        return DefaultHttpCall.syncCallResponse(getRequest(wrapper));
    }

    // async handle all content-type call return Future<String>
    public static Future<String> asyncCall(@NotNull HttpRequestWrapper wrapper) {
        switch (wrapper.httpClientType()) {
            case OK_HTTP:
                return DefaultHttpCall.asyncCall(getRequest(wrapper));
            default:
                throw new RuntimeException("not_supported_http_client_type");
        }
    }

    // async handle application/json call return Future<T>
    public static <T> Future<T> asyncCall(@NotNull HttpRequestWrapper wrapper, @NotNull Class<T> clazz) {
        switch (wrapper.httpClientType()) {
            case OK_HTTP:
                return DefaultHttpCall.asyncCall(getRequest(wrapper), clazz);
            default:
                throw new RuntimeException("not_supported_http_client_type");
        }
    }

    // async all call return Future<Response>
    public static Future<Response> asyncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper) {
        return DefaultHttpCall.asyncCallResponse(getRequest(wrapper));
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
