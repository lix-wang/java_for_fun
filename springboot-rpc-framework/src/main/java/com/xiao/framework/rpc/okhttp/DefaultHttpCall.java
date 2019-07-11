package com.xiao.framework.rpc.okhttp;

import com.xiao.framework.rpc.async.DefaultAsyncFactory;
import com.xiao.framework.rpc.model.AsyncResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Default http call.
 *
 * @author lix wang
 */
public class DefaultHttpCall {
    private static final Logger logger = LogManager.getLogger(DefaultHttpCall.class);

    public static String syncCall(@NotNull Request request) throws IOException {
        HttpExecutionWrapper wrapper = HttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultConnectionPool.getDefaultClient()).build();
        return OkHttpCall.syncCall(wrapper);
    }

    public static <T> T syncCall(@NotNull Request request, @NotNull Class<T> clazz) throws IOException {
        HttpExecutionWrapper wrapper = HttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultConnectionPool.getDefaultClient()).build();
        return OkHttpCall.syncCall(wrapper, clazz);
    }

    public static Response syncCallResponse(@NotNull Request request) throws IOException {
        HttpExecutionWrapper wrapper = HttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultConnectionPool.getDefaultClient()).build();
        return OkHttpCall.syncCallResponse(wrapper);
    }

    public static Future<String> asyncCall(@NotNull Request request) {
        HttpExecutionWrapperAndCallback param = constructAsyncParam(request);
        return OkHttpCall.asyncCall(param.getWrapper(), param.getCallback());
    }

    public static <T> Future<T> asyncCall(@NotNull Request request, @NotNull Class<T> clazz) {
        HttpExecutionWrapperAndCallback param = constructAsyncParam(request);
        return OkHttpCall.asyncCall(param.getWrapper(), clazz, param.getCallback());
    }

    public static Future<Response> asyncCallResponse(@NotNull Request request) {
        HttpExecutionWrapperAndCallback param = constructAsyncParam(request);
        return OkHttpCall.asyncCallResponse(param.getWrapper(), param.getCallback());
    }

    private static HttpExecutionWrapperAndCallback constructAsyncParam(@NotNull Request request) {
        AsyncResult<String> asyncResult = DefaultAsyncFactory.getAsyncContext();
        if (asyncResult != null) {
            asyncResult.setStartTime(System.currentTimeMillis());
        }
        HttpExecutionWrapper wrapper = HttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DefaultConnectionPool.getDefaultClient()).build();
        Callback callback = getDefaultCallback(asyncResult);
        return HttpExecutionWrapperAndCallback.builder()
                .wrapper(wrapper)
                .callback(callback).build();
    }

    private static Callback getDefaultCallback(@NotNull AsyncResult asyncResult) {
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.error("Async call failed " + e.getMessage());
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (asyncResult != null) {
                    asyncResult.setEndTime(System.currentTimeMillis());
                }
            }
        };
    }
}
