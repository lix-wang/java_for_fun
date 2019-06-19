package com.xiao.biz.interceptor;

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
        return true;
    }

    protected abstract boolean checkActuatorAccessPermission();
}
