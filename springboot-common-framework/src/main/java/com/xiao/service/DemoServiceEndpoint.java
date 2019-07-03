package com.xiao.service;

import lombok.extern.log4j.Log4j2;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Expose web service.
 *
 * @author lix wang
 */
@Log4j2
@WebService(serviceName = "demoService")
public class DemoServiceEndpoint {
    @WebMethod
    public  void saveSomething(String inputStr) {
        log.info("save something: " + inputStr);
    }
}
