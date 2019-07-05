package com.xiao.framework.rpc.demo;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Demo interceptor to gzip body.
 *
 * @author lix wang
 */
public class DemoGzipInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() == null || request.header("Content-Encoding") != null) {
            return chain.proceed(request);
        }
        Request compressRequest = request.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(request.method(), gzip(request.body())).build();
        return chain.proceed(compressRequest);
    }

    private RequestBody gzip(final RequestBody requestBody) {
        return new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() throws IOException {
                return -1;
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(bufferedSink));
                requestBody.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
