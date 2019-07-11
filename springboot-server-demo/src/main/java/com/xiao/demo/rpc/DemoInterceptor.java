package com.xiao.demo.rpc;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Demo interceptor fof OkHttp
 *
 * @author lix wang
 */
public class DemoInterceptor implements Interceptor {
    private static final Logger logger = LogManager.getLogger(DemoInterceptor.class);

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = chain.request();
        logger.info(String.format("Sending  request %s on %s with headers %s",
                request.url(), chain.connection(), request.headers()));
        Response response = chain.proceed(request);
        logger.info("Processed the request for %s with headers %s use %n ms",
                response.request().url(), response.headers(), (System.currentTimeMillis() - startTime));
        // Rewriting response.
        return response.newBuilder()
                .header("Cache-Control", "max-age=60").build();
    }
}
