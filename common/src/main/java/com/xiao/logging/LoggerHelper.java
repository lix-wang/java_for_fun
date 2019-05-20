package com.xiao.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This helper is to handle logger.
 *
 * @author lix wang
 */
public class LoggerHelper {
    public static final LoggerContext LOGGER_CONTEXT = (LoggerContext) LogManager.getContext(false);
    public static final Configuration LOG_CONFIGURATION = LOGGER_CONTEXT.getConfiguration();

    /**
     * Get logger.
     * If exist, get directly, otherwise, return null.
     */
    public static Logger getLogger(@NotNull String loggerName) {
        if (!LOG_CONFIGURATION.getLoggers().containsKey(loggerName)) {
            return null;
        } else {
            return LogManager.getLogger(loggerName);
        }
    }

    public static boolean deleteLogger(@NotNull String loggerName) {
        LOG_CONFIGURATION.getAppender(loggerName).stop();
        LOG_CONFIGURATION.getLoggerConfig(loggerName).removeAppender(loggerName);
        LOG_CONFIGURATION.removeLogger(loggerName);
        LOGGER_CONTEXT.updateLoggers();
        return true;
    }

    /**
     * Create logger
     */
    public static void createLogger(@NotNull List<AppenderLevelMapping> appenderLevelMappings,
            @NotNull String loggerName) {
        // first filter out not nullable elements
        appenderLevelMappings = appenderLevelMappings.stream().filter(appenderLevelMapping ->
                appenderLevelMapping.getAppender() != null && appenderLevelMapping.getLevel() != null)
                .collect(Collectors.toList());
        // create appenderRefs
        List<AppenderRef> appenderRefs = appenderLevelMappings.stream().map(appenderLevelMapping -> {
            appenderLevelMapping.getAppender().start();
            return AppenderRef.createAppenderRef(appenderLevelMapping.getAppender().getName(),
                    appenderLevelMapping.getLevel(), null);
        }).collect(Collectors.toList());
        // create loggerConfig
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.ALL, loggerName,
                "true", appenderRefs.stream().toArray(AppenderRef[]::new), null,
                LOG_CONFIGURATION, null);
        // loggerConfig add appenders
        appenderLevelMappings.forEach(appenderLevelMapping -> loggerConfig.addAppender(
                appenderLevelMapping.getAppender(), appenderLevelMapping.getLevel(), null));
        // logging configuration add logger
        LOG_CONFIGURATION.addLogger(loggerName, loggerConfig);
        LOGGER_CONTEXT.updateLoggers();
    }
}
