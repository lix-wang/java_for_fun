package com.xiao.interceptor;

import com.xiao.framework.biz.actuator.AbstractActuatorInterceptor;
import com.xiao.event.ActuatorPublisher;
import com.xiao.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.servlet.WebMvcEndpointManagementContextConfiguration;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lix wang
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActuatorInterceptorConfiguration extends WebMvcEndpointManagementContextConfiguration {
    private final ActuatorPublisher actuatorPublisher;
    @Override
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
            ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
            EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
            WebEndpointProperties webEndpointProperties) {
        WebMvcEndpointHandlerMapping handlerMapping = super.webEndpointServletHandlerMapping(webEndpointsSupplier,
                servletEndpointsSupplier, controllerEndpointsSupplier, endpointMediaTypes, corsProperties,
                webEndpointProperties);
        handlerMapping.setInterceptors(new AbstractActuatorInterceptor() {
            @Override
            protected void handleHook(HttpServletRequest request) {
                actuatorPublisher.publishActuatorEvent(User.builder().name("Admin").build(), request.getRequestURI());
            }

            @Override
            protected boolean checkActuatorAccessPermission() {
                // return actuatorHelper.checkActuatorAccessPermission();
                return true;
            }
        });
        return handlerMapping;
    }
}
