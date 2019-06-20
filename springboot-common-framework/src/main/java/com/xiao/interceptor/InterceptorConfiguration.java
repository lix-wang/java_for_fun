package com.xiao.interceptor;

import com.xiao.helper.ActuatorHelper;
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
    private final ActuatorHelper actuatorHelper;

    @Autowired
    public InterceptorConfiguration(SessionService sessionService, ActuatorHelper actuatorHelper) {
        this.sessionService = sessionService;
        this.actuatorHelper = actuatorHelper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor(sessionService));
        // This place works the same as ActuatorInterceptorConfiguration does.
        // registry.addInterceptor(new ActuatorInterceptor(actuatorHelper));
    }
}
