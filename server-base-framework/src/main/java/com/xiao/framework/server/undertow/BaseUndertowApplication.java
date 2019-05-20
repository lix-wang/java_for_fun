package com.xiao.framework.server.undertow;

import com.xiao.framework.server.common.BaseApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author lix wang
 */
public class BaseUndertowApplication extends BaseApplication {

    protected static ConfigurableApplicationContext start(Class<?> primarySource, String[] args) {
        ConfigurableApplicationContext context = run(primarySource, args);
        // start to set up logging

        return context;
    }
}
