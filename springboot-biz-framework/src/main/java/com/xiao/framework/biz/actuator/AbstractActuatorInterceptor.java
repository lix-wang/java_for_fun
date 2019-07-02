package com.xiao.framework.biz.actuator;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lix wang
 */
public abstract class AbstractActuatorInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getRequestURI().startsWith("/actuator")) {
            if (!checkActuatorAccessPermission()) {
                throw new RuntimeException("actuator.access_denied");
            }
        }
        handleHook(request);
        return true;
    }

    protected abstract void handleHook(HttpServletRequest request);

    protected abstract boolean checkActuatorAccessPermission();
}
