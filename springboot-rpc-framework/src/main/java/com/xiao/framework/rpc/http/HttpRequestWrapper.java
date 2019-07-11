package com.xiao.framework.rpc.http;

import java.util.LinkedHashMap;

/**
 * Wrapper for http request.
 *
 * @author lix wang
 */
public class HttpRequestWrapper {
    private LinkedHashMap<String, String> requestParameters;
    private LinkedHashMap<String, String> headers;

    private String url;
    private HttpMethodType httpMethodType;
    private HttpClientType httpClientType = HttpClientType.OK_HTTP;

    public String url() {
        return this.url;
    }

    public HttpMethodType httpType() {
        return this.httpMethodType;
    }

    public LinkedHashMap<String, String> parameters() {
        return this.requestParameters;
    }

    public LinkedHashMap<String, String> headers() {
        return this.headers;
    }

    public HttpClientType httpClientType() {
        return this.httpClientType;
    }

    public static Builder builder() {
        return new Builder();
    }

    private HttpRequestWrapper(Builder builder) {
        this.url = builder.url;
        this.httpMethodType = builder.httpMethodType;
        this.requestParameters = builder.requestParameters;
        this.headers = builder.headers;
        this.httpClientType = builder.httpClientType;
    }

    public static final class Builder {
        private LinkedHashMap<String, String> requestParameters = new LinkedHashMap<>();
        private LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        private String url;
        private HttpMethodType httpMethodType;
        private HttpClientType httpClientType;

        public void url(String url) {
            this.url = url;
        }

        public void httpType(HttpMethodType httpMethodType) {
            this.httpMethodType = httpMethodType;
        }

        public void parameter(String name, String value) {
            this.requestParameters.put(name, value);
        }

        public void header(String name, String value) {
            this.headers.put(name, value);
        }

        public void httpClientType(HttpClientType httpClientType) {
            this.httpClientType = httpClientType;
        }

        public HttpRequestWrapper build() {
            return new HttpRequestWrapper(this);
        }
    }
}
