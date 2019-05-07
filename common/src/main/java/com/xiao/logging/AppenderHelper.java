package com.xiao.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.validation.constraints.NotNull;

/**
 * Create different appenders
 *
 * @author lix wang
 */
public class AppenderHelper {
    /**
     * Create RandomAccessFileAppender by pattern and destination fileName
     */
    public static RandomAccessFileAppender createRandomAccessFileAppender(@NotNull LogPatternLayoutEnum pattern,
            @NotNull String fileName, @NotNull String appenderName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        PatternLayout patternLayout = PatternLayout.newBuilder().withConfiguration(LoggerHelper.LOG_CONFIGURATION)
                .withPattern(pattern.getPattern()).build();
        RandomAccessFileAppender appender = RandomAccessFileAppender.newBuilder()
                .withName(appenderName)
                .setFileName(fileName)
                .withLayout(patternLayout)
                .setConfiguration(LoggerHelper.LOG_CONFIGURATION)
                .build();
        return appender;
    }

    public static ConsoleAppender createConsoleAppender(@NotNull LogPatternLayoutEnum pattern,
            @NotNull String appenderName) {
        PatternLayout patternLayout = PatternLayout.newBuilder().withPattern(pattern.getPattern()).build();
        ConsoleAppender appender = ConsoleAppender.newBuilder()
                .withName(appenderName)
                .withLayout(patternLayout)
                .setTarget(Target.SYSTEM_OUT)
                .build();
        return appender;
    }
}
