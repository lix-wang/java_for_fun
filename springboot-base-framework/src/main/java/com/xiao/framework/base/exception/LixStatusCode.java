package com.xiao.framework.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception status code enum.
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum LixStatusCode {
    OK(200, "OK"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone");

    final int statusCode;
    final String statusMessage;
}
