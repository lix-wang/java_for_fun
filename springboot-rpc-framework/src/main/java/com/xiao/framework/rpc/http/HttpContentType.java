package com.xiao.framework.rpc.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Http content type header enum.
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum HttpContentType {
    APPLICATION_JSON("application/json; charset=utf-8");

    private final String contentType;
}
