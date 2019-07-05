package com.xiao.framework.rpc.demo;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Demo synchronous get with OkHttp.
 *
 * @author lix wang
 */
public class DemoSynchronousGet {
    public void run() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/").build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response " + response);
            }
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                System.out.println(headers.name(i) + ": " + headers.value(i));
            }
            System.out.println(response.body().string());
        }
    }

    public void timeoutRun() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response completed " + response);
        }
    }

    public void newBuilderRun() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        OkHttpClient okHttpClient1 = client.newBuilder()
                .readTimeout(20, TimeUnit.SECONDS).build();
        try (Response response = okHttpClient1.newCall(request).execute()) {
            System.out.println("Response 1 succeed " + response);
        }

        OkHttpClient okHttpClient2 = client.newBuilder()
                .readTimeout(10, TimeUnit.SECONDS).build();
        try (Response response = okHttpClient2.newCall(request).execute()) {
            System.out.println("Response 2 succeed " + response);
        }
    }

    public void authenticateRun() throws IOException {
        OkHttpClient client = new Builder()
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                        if (response.request().header("Authorization") != null) {
                            return null;
                        }
                        if (responseCount(response) >= 3) {
                            return null;
                        }
                        System.out.println("Authenticate failure response " + response);
                        String credential = Credentials.basic("a", "b");
                        return response.request().newBuilder()
                                .header("Authorization", credential).build();
                    }
                }).build();

        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response " + response);
            }
            System.out.println(response.body().string());
        }
    }

    public void applicationInterceptorRun() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new DemoInterceptor()).build();
        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
            response.body().close();
        }
    }

    public void networkInterceptorRun() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new DemoInterceptor()).build();
        Request request = new Request.Builder()
                .url("https://www.baidu.com").build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
            response.body().close();
        }
    }

    /**
     * 获取当前Response是请求了几次后的Response
     */
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
