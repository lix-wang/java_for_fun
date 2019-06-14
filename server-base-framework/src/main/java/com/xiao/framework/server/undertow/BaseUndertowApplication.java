package com.xiao.framework.server.undertow;

import com.google.common.base.CaseFormat;
import com.xiao.framework.server.common.BaseApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author lix wang
 */
public class BaseUndertowApplication extends BaseApplication {
    protected static ConfigurableApplicationContext start(Class<?> primarySource, String[] args) {
        System.setProperty("logging.config", "classpath:log4j2-spring.xml");
        System.setProperty("log.file.basename",
                CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, primarySource.getSimpleName()));
        System.setProperty("management.endpoints.web.exposure.include", "*");
        return run(primarySource, args);
    }
}
