package com.xiao.interceptor;

import com.xiao.service.SessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lix wang
 */
@Log4j2
public class SessionInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        SessionUtils.CURRENT_USER.set(sessionService.getBySession(request));
        return true;
    }
}
