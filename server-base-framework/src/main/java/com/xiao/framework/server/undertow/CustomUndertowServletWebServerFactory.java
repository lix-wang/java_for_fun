package com.xiao.framework.server.undertow;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.util.Collections;
import java.util.Set;

/**
 * Custom UndertowServletWebServerFactory.
 *
 * @author lix wang
 */
public class CustomUndertowServletWebServerFactory extends AbstractServletWebServerFactory
        implements ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        DeploymentManager manager = createDeploymentManager(initializers);
        int port = getPort();
        Builder builder = createBuilder(port);
        return getUndertowWebServer(builder, manager, port);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private DeploymentManager createDeploymentManager(ServletContextInitializer... initializers) {
        DeploymentInfo deployment = Servlets.deployment();
        Initializer initializer = new Initializer(initializers);
        deployment.addServletContainerInitalizers(new ServletContainerInitializerInfo(Initializer.class,
                new ImmediateInstanceFactory<ServletContainerInitializer>(initializer), Collections.emptySet()));
        // todo custom rootHttpHandler.
        return null;
    }

    private Builder createBuilder(int port) {
        Builder builder = Undertow.builder();
        builder.addHttpListener(port, getListenAddress());
        return builder;
    }

    private String getListenAddress() {
        if (getAddress() == null) {
            return "0.0.0.0";
        } else {
            return getAddress().getHostAddress();
        }
    }

    protected UndertowServletWebServer getUndertowWebServer(Builder builder,
            DeploymentManager manager, int port) {
        return new UndertowServletWebServer(builder, manager, getContextPath(),
                false, port >= 0, getCompression(), getServerHeader());
    }

    private static class Initializer implements ServletContainerInitializer {
        private final ServletContextInitializer[] initializers;

        Initializer(ServletContextInitializer[] initializers) {
            this.initializers = initializers;
        }

        @Override
        public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
                throws ServletException {
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(servletContext);
            }
        }
    }
}
