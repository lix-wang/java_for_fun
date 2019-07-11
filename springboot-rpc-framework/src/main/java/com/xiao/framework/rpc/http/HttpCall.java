package com.xiao.framework.rpc.http;

import com.xiao.framework.rpc.okhttp.DefaultHttpCall;
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
 * Http call.
 *
 * @author lix wang
 */
public class HttpCall {
    // handle all content-type sync okhttp call return response
    public static Response syncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper) throws IOException {
        return DefaultHttpCall.syncCallResponse(getRequest(wrapper));
    }

    // async all call return Future<Response>
    public static Future<Response> asyncCallWithOkHttp(@NotNull HttpRequestWrapper wrapper,
            @NotNull Callback callback) {
        return DefaultHttpCall.asyncCallResponse(getRequest(wrapper), callback);
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
