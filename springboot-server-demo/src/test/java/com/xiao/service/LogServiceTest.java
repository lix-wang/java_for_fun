package com.xiao.service;

import com.xiao.demo.server.SpringDemoServer;
import com.xiao.framework.biz.environment.CmdLineConfig;
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
    private CmdLineConfig cmdLineConfig;

    @Test
    public void testPrintLog() {
        Logger log = LoggerFactoryService.getLogger(LoggerTypeEnum.DEFAULT_LOGGER, cmdLineConfig.getProfile());
        log.trace("I am a trace logging");
        log.debug("I am a debug logging");
        log.info("I am a info logging");
        log.warn("I am a warn logging");
        log.error("I am a error logging");
        log.fatal("I am a fatal logging");
    }
}
