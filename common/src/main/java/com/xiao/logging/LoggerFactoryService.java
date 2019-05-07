package com.xiao.logging;

import com.xiao.environment.CmdLineConfig;
import com.xiao.utils.JodaUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factory to create specific logger
 *
 * @author lix wang
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoggerFactoryService {
    private static final String DEFAULT_LOG_FILE_DIR = "logs/";
    private static final String DEFAULT_LOGGER_NAME = "defaultLogger";
    private static final String DEFAULT_RANDOM_ACCESS_FILE_APPENDER_NAME = "defaultRandomAccessFileAppender";
    private static final String DEFAULT_CONSOLE_APPENDER_NAME = "defaultConsoleAppender";
    private final CmdLineConfig cmdLineConfig;

    public Logger getLogger(@NotNull LoggerTypeEnum loggerType) {
        switch (loggerType) {
            case DEFAULT_LOGGER:
                return getDefaultLogger();
            default:
                throw new RuntimeException("logger.not_supported_logger_type");

        }
    }

    /**
     * Get default logger.
     * Include console appender and randomAccessFile appender
     */
    private Logger getDefaultLogger() {
        // get default logger
        Logger logger = LoggerHelper.getLogger(DEFAULT_LOGGER_NAME);
        // If exists default logger, then check formats
        if (logger != null) {
            if (checkDefaultLoggerAppenders()) {
                return logger;
            } else {
                LoggerHelper.deleteLogger(DEFAULT_LOGGER_NAME);
                createDefaultLogger();
            }
        } else {
            createDefaultLogger();
        }
        return LoggerHelper.getLogger(DEFAULT_LOGGER_NAME);
    }

    /**
     * check default logger's appender whether match our expectations
     */
    private boolean checkDefaultLoggerAppenders() {
        LoggerConfig loggerConfig = LoggerHelper.LOG_CONFIGURATION.getLoggerConfig(DEFAULT_LOGGER_NAME);
        Map<String, Appender> appenderMap = loggerConfig.getAppenders();
        if (appenderMap.containsKey(DEFAULT_LOGGER_NAME)) {
            RandomAccessFileAppender accessFileAppender =
                    (RandomAccessFileAppender) appenderMap.get(DEFAULT_RANDOM_ACCESS_FILE_APPENDER_NAME);
            return computeDefaultFileName().equals(accessFileAppender.getFileName());
        }
        return false;
    }

    /**
     * Create default logger include randomAccessFileAppender and console appender.
     * Actually you can use DailyRollingFileAppender
     * or RollingRandomAccessFile set Policies as TimeBasedTriggeringPolicy
     */
    private void createDefaultLogger() {
        List<AppenderLevelMapping> appenderLevelMappings = new ArrayList<>();
        RandomAccessFileAppender appender = AppenderHelper.createRandomAccessFileAppender(
                LogPatternLayoutEnum.DEFAULT_PATTERN, computeDefaultFileName(),
                DEFAULT_RANDOM_ACCESS_FILE_APPENDER_NAME);
        appenderLevelMappings.add(AppenderLevelMapping.builder().appender(appender).level(Level.INFO).build());
        ConsoleAppender consoleAppender = AppenderHelper.createConsoleAppender(LogPatternLayoutEnum.CONSOLE_PATTERN,
                DEFAULT_CONSOLE_APPENDER_NAME);
        appenderLevelMappings.add(AppenderLevelMapping.builder().appender(consoleAppender).level(Level.ALL).build());
        LoggerHelper.createLogger(appenderLevelMappings, "defaultLogger");
    }

    private String computeDefaultFileName() {
        return DEFAULT_LOG_FILE_DIR + new DateTime().toString(JodaUtils.DAY_FORMAT_NO_HYPHEN)
                + "_" + cmdLineConfig.getProfile().getName();
    }
}
