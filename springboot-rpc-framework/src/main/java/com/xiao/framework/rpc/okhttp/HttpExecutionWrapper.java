package com.xiao.framework.rpc.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Wrapper for http request.
 *
 * @author lix wang
 */
public class HttpExecutionWrapper {
    private OkHttpClient okHttpClient;
    private Request request;

    public static Builder builder() {
        return new Builder();
    }

    public OkHttpClient okHttpClient() {
        return this.okHttpClient;
    }

    public Request request() {
        return this.request;
    }

    private HttpExecutionWrapper(Builder builder) {
        this.okHttpClient = builder.okHttpClient;
        this.request = builder.request;
    }

    public static final class Builder {
        private OkHttpClient okHttpClient;
        private Request request;

        public Builder() {
        }

        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        public HttpExecutionWrapper build() {
            return new HttpExecutionWrapper(this);
        }
    }
}
