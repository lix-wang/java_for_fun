package com.xiao.framework.base.utils;

import com.xiao.framework.base.exception.LixException;
import com.xiao.framework.base.exception.LixStatusCode;

/**
 * @author lix wang
 */
public class Assert {
    public static void check(boolean checkValue, String message) {
        if (!checkValue) {
            throw LixException.builder()
                    .message(message)
                    .statusCode(LixStatusCode.FORBIDDEN).build()
                    .toRuntimeException();
        }
    }
}
