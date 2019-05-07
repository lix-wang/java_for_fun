package com.xiao.service;

import com.xiao.SpringDemoServer;
import com.xiao.log.LoggerFactoryService;
import com.xiao.log.LoggerTypeEnum;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lix wang
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringDemoServer.class)
public class LogServiceTest {
    @Autowired
    private LoggerFactoryService loggerFactoryService;

    @Test
    public void testPrintLog() {
        Logger log = loggerFactoryService.getLogger(LoggerTypeEnum.DEFAULT_LOGGER);
        log.trace("I am a trace log");
        log.debug("I am a debug log");
        log.info("I am a info log");
        log.warn("I am a warn log");
        log.error("I am a error log");
        log.fatal("I am a fatal log");
    }
}
