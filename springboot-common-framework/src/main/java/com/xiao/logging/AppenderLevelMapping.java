package com.xiao.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;

/**
 * Build mapping between Appender and Level.
 *
 * @author lix wang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppenderLevelMapping {
    private AbstractOutputStreamAppender appender;
    private Level level;
}
