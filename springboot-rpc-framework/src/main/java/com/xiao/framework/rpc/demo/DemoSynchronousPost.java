package com.xiao.framework.rpc.demo;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author lix wang
 */
public class DemoSynchronousPost {
    private static MediaType TYPE = MediaType.parse("text/x-markdown; charset=utf-8");

    public void run() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String requestBody = "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";

        Request request = new Request.Builder()
                .url("xxx")
                .post(RequestBody.create(requestBody, TYPE)).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response " + response);
            }
            System.out.println(response.body().string());
        }
    }

    public void streamRun() throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return TYPE;
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                for (int i = 0; i < 100; i++) {
                    bufferedSink.writeUtf8("I am line " + i);
                }
            }
        };
        Request request = new Request.Builder()
                .url("xxx")
                .post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response " + response);
            }
            System.out.println(response.body().string());
        }
    }

    public void fileRun() throws IOException {
        OkHttpClient client = new OkHttpClient();
        File file = new File("README.md");
        Request request = new Request.Builder()
                .url("xxx")
                .post(RequestBody.create(file, TYPE)).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response " + response);
            }
            System.out.println(response.body().string());
        }
    }

    public void mutipartRun() throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "logo")
                .addFormDataPart("image", "a.png", RequestBody.create(mediaType, new File("xxx"))).build();
        Request request = new Request.Builder()
                .url("xxx")
                .post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected response " + response);
            }
            System.out.println(response.body().string());
        }
    }


}
