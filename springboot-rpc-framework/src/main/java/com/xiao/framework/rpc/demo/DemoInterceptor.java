package com.xiao.framework.rpc.demo;

import lombok.extern.log4j.Log4j2;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Demo interceptor fof OkHttp
 *
 * @author lix wang
 */
@Log4j2
public class DemoInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = chain.request();
        log.info(String.format("Sending  request %s on %s with headers %s",
                request.url(), chain.connection(), request.headers()));
        Response response = chain.proceed(request);
        log.info("Processed the request for %s with headers %s use %n ms",
                response.request().url(), response.headers(), (System.currentTimeMillis() - startTime));
        // Rewriting response.
        return response.newBuilder()
                .header("Cache-Control", "max-age=60").build();
    }
}
