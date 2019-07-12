package com.xiao.demo.rpc;

import com.xiao.framework.rpc.model.AbstractAsyncResult;
import com.xiao.framework.rpc.model.AsyncResult;
import com.xiao.framework.rpc.okhttp.OkHttpCall;
import com.xiao.framework.rpc.okhttp.OkHttpExecutionWrapper;
import com.xiao.framework.rpc.async.AsyncCall;
import com.xiao.framework.rpc.async.DefaultAsyncFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Demo http callAsync.
 *
 * @author lix wang
 */
public class DemoHttpCall {
    public static String syncCall() {
        String result = null;
        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        OkHttpExecutionWrapper wrapper = OkHttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DemoConnectionPool.getDefaultClient()).build();
        try {
            result = OkHttpCall.syncCall(wrapper);
            System.out.println("I received a response: " + result);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Future<String> asyncCall() {
        AsyncResult<String> asyncResult = DefaultAsyncFactory.getAsyncContext();
        asyncResult.setStartTime(System.currentTimeMillis());
        Future<String> resultFuture;
        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        OkHttpExecutionWrapper wrapper = OkHttpExecutionWrapper.builder()
                .request(request)
                .okHttpClient(DemoConnectionPool.getDefaultClient()).build();
        resultFuture = OkHttpCall.asyncCall(wrapper, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Async callAsync failed " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                asyncResult.setEndTime(System.currentTimeMillis());
                System.out.println("Finish onResponse.");
            }
        });
        return resultFuture;
    }

    public static void main(String[] args) {
        // AbstractAsyncResult<String> abstractAsyncResult = null;
        // try {
        //     abstractAsyncResult = AsyncCall.asyncCall(() -> syncCall());
        //     System.out.println("I am printing something.");
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // System.out.println("I finished a asyncCall " + abstractAsyncResult.get());

        AbstractAsyncResult<String> abstractAsyncResult1 = null;
        try {
            abstractAsyncResult1 = AsyncCall.callAsync(() -> asyncCall());
            System.out.println("I am started a callAsync");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("I finished a callAsync startTime " + abstractAsyncResult1.get());
    }
}
