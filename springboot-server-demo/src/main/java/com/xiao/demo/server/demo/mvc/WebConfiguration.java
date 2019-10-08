package com.xiao.demo.server.demo.mvc;

import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Configuration for DispatcherServlet.
 *
 * @author lix wang
 */
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver resourceViewResolver = new InternalResourceViewResolver();
        resourceViewResolver.setPrefix("/WEB-INF/views/*");
        resourceViewResolver.setSuffix("*.jsp");
        resourceViewResolver.setExposeContextBeansAsAttributes(true);
        registry.viewResolver(resourceViewResolver);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // DispatcherServlet 对静态资源的请求转发到Servlet容器中默认的Servlet上。
        configurer.enable();
    }
}
