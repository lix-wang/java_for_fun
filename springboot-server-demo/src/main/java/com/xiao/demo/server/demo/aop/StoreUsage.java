package com.xiao.demo.server.demo.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactoryBean;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public class StoreUsage {
    private void run() {
        Advice before = new StoreBeforeAdvice();
        Advice after = new StoreAfterReturningAdvice();
        Advice around = new StoreAroundAdvice();
        Advice throwsAdvice = new StoreThrowsAdvice();

        StoreService storeService = new StoreServiceImpl();

        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(storeService);
        proxyFactoryBean.setProxyTargetClass(true);

        proxyFactoryBean.addAdvice(after);
        proxyFactoryBean.addAdvice(around);
        proxyFactoryBean.addAdvice(throwsAdvice);
        proxyFactoryBean.addAdvice(before);

        StoreService storeService1 = (StoreService) proxyFactoryBean.getObject();
        storeService1.buy();
    }

    public static void main(String[] args) {
        StoreUsage storeUsage = new StoreUsage();
        storeUsage.run();
    }
}
