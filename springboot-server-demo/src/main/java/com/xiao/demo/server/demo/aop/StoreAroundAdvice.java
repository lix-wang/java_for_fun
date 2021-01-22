package com.xiao.demo.server.demo.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public class StoreAroundAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println(".....around advice begin.....");
        Object result = invocation.proceed();
        System.out.println(".....around advice end.....");
        return result;
    }
}
