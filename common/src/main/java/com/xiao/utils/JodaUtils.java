package com.xiao.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author lix wang
 */
public class JodaUtils {
    public static final DateTimeFormatter DAY_FORMAT_NO_HYPHEN = DateTimeFormat.forPattern("yyyyMMdd");
    public static final long MILLONS_PER_SECOND = 1000;
    public static final long SENCONDS_PER_MINUTE = 60;
    public static final long MINUTES_PER_HOUR = 60;
    public static final long HOURS_PER_DAY = 24;
}
