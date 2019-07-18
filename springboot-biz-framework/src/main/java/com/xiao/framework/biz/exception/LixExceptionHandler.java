package com.xiao.framework.biz.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handler to handle runtime exceptions.
 *
 * @author lix wang
 */
@ResponseBody
@ControllerAdvice
public class LixExceptionHandler {
    private static final Logger logger = LogManager.getLogger(LixExceptionHandler.class);

    @ExceptionHandler(LixRuntimeException.class)
    public LixRuntimeException handle(LixRuntimeException exception) {
        logger.error(String.format("%s %d errorCode: %s message: %s", exception.getClass().getSimpleName(),
                exception.getStatusCode(), exception.getErrorCode(), exception.getMessage()), exception);
        return exception;
    }
}
