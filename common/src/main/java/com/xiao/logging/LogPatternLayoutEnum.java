package com.xiao.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of PatternLayout.pattern
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum LogPatternLayoutEnum {
    DEFAULT_PATTERN("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p %m%n%ex"),
    CONSOLE_PATTERN("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p ConsoleAppender %m%n%ex");

    private String pattern;
}
