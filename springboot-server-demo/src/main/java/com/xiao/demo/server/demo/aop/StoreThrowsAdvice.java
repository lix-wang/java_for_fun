package com.xiao.demo.server.demo.aop;

import org.springframework.aop.ThrowsAdvice;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public class StoreThrowsAdvice implements ThrowsAdvice {
    public void afterThrowing(Exception ex) {
        System.out.println(".....after throwing.....");
    }
}
