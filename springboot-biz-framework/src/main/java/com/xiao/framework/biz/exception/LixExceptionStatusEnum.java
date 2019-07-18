package com.xiao.framework.biz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception status code enum.
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum LixExceptionStatusEnum {
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405);

    private final int statusCode;
}
