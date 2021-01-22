package com.xiao.demo.server.demo.aop;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public class StoreBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(".....before advice.....");
    }
}
