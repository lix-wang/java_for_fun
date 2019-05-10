package com.xiao.service;

import com.xiao.SpringDemoServer;
import com.xiao.environment.CmdLineConfig;
import com.xiao.logging.LoggerFactoryService;
import com.xiao.logging.LoggerTypeEnum;
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
    @Autowired
    private CmdLineConfig cmdLineConfig;

    @Test
    public void testPrintLog() {
        Logger log = loggerFactoryService.getLogger(LoggerTypeEnum.DEFAULT_LOGGER, cmdLineConfig.getProfile());
        log.trace("I am a trace logging");
        log.debug("I am a debug logging");
        log.info("I am a info logging");
        log.warn("I am a warn logging");
        log.error("I am a error logging");
        log.fatal("I am a fatal logging");
    }
}
