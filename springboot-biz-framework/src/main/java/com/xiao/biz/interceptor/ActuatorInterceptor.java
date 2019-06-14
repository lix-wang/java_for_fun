package com.xiao.biz.interceptor;

import com.xiao.biz.service.ActuatorService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lix wang
 */
public class ActuatorInterceptor implements HandlerInterceptor {
    private final ActuatorService actuatorService;

    public ActuatorInterceptor(ActuatorService actuatorService) {
        this.actuatorService = actuatorService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getRequestURI().startsWith("/actuator")) {
            if (!actuatorService.checkActuatorAccessPermission()) {
                throw new RuntimeException("actuator.access_denied");
            }
        }
        return true;
    }
}
