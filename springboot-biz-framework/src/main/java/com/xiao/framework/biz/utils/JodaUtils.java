package com.xiao.framework.biz.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;

/**
 *
 * @author lix wang
 */
public class JodaUtils {
    public static final long MILLONS_PER_SECOND = 1000;
    public static final long SENCONDS_PER_MINUTE = 60;
    public static final long MINUTES_PER_HOUR = 60;
    public static final long HOURS_PER_DAY = 24;

    public static DateTimeZone SH_TIME_ZONE = DateTimeZone.forID("Asia/Shanghai");
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DAY_FORMAT_NO_HYPHEN = DateTimeFormat.forPattern("yyyyMMdd");
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormat.forPattern(
            DEFAULT_DATETIME_PATTERN);

    public static DateTime getCSTDateTime(@NotNull String dateTimeStr) {
        return DateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER).toDateTime(SH_TIME_ZONE);
    }

    public static DateTime getUtcDateTime(@NotNull String dateTimeStr) {
        return DateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER).toDateTime(DateTimeZone.UTC);
    }
}
