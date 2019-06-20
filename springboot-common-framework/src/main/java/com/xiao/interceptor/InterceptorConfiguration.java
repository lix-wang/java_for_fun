package com.xiao.interceptor;

import com.xiao.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author lix wang
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    private final SessionService sessionService;

    @Autowired
    public InterceptorConfiguration(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor(sessionService));
        // This place works the same as ActuatorInterceptorConfiguration does.
        // registry.addInterceptor(new ActuatorInterceptor(actuatorHelper));
    }
}
