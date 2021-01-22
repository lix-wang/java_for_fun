package com.xiao.demo.server.demo.aop;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public class StoreAfterReturningAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println(".....after returning advice.....");
    }
}
