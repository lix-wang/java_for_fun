package com.xiao.framework.rpc.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Http type enum.
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum HttpMethodType {
    GET,
    POST,
    PUT,
    DELETE;
}
