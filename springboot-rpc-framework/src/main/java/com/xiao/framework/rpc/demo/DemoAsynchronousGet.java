package com.xiao.framework.rpc.demo;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Demo asynchronous get with OkHttp.
 *
 * @author lix wang
 */
public class DemoAsynchronousGet {
    public void run() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
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
        });
    }

    public void runWithEventListener() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .eventListener(new DemoEventListener()).build();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    System.out.println("Response body is " + responseBody.string());
                }
            }
        });
    }

    public static void main(String[] args) {
        DemoAsynchronousGet demoAsynchronousGet = new DemoAsynchronousGet();
        try {
            demoAsynchronousGet.runWithEventListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
