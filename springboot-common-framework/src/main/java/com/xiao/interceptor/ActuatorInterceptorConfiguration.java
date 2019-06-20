package com.xiao.interceptor;

import com.xiao.framework.biz.interceptor.AbstractActuatorInterceptor;
import com.xiao.helper.ActuatorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author lix wang
 */
@Configuration
public class ActuatorInterceptorConfiguration implements WebMvcConfigurer {
    private final ActuatorHelper actuatorHelper;

    @Autowired
    public ActuatorInterceptorConfiguration(ActuatorHelper actuatorHelper) {
        this.actuatorHelper = actuatorHelper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AbstractActuatorInterceptor() {
            @Override
            protected boolean checkActuatorAccessPermission() {
                return actuatorHelper.checkActuatorAccessPermission();
            }
        });
    }
}
