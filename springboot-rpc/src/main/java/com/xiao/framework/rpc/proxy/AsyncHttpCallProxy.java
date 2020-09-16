package com.xiao.framework.rpc.proxy;

import com.xiao.framework.rpc.async.DefaultAsyncFactory;
import com.xiao.framework.rpc.http.BaseHttpCall;
import com.xiao.framework.rpc.http.HttpRequestWrapper;
import com.xiao.framework.rpc.model.AsyncResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Proxy for {@link BaseHttpCall#asyncCallWithOkHttp(HttpRequestWrapper)} method.
 *
 * @author lix wang
 */
public class AsyncHttpCallProxy<T> extends AbstractInvocationHandler<T> {
    private static final Logger logger = LogManager.getLogger(AsyncHttpCallProxy.class);

    private Callback callback;

    public AsyncHttpCallProxy(Class<T> targetClass, T targetObject) {
        super(targetClass, targetObject);
    }

    public AsyncHttpCallProxy(Class<T> targetClass, T targetObject, Callback callback) {
        super(targetClass, targetObject);
        this.callback = callback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        AsyncResult<T> asyncResult = DefaultAsyncFactory.getAsyncContext();
        if (asyncResult != null) {
            asyncResult.setStartTime(System.currentTimeMillis());
        }
        BaseHttpCall.ASYNC_HTTP_CALLBACK.set(getAsyncHttpCallback(asyncResult));
        Object result = method.invoke(getTargetObject(), args);
        BaseHttpCall.ASYNC_HTTP_CALLBACK.set(null);
        return result;
    }

    private Callback getAsyncHttpCallback(AsyncResult asyncResult) {
        if (this.callback != null) {
            return this.callback;
        }
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.error("Async http call failed " + e.getMessage(), e);
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
