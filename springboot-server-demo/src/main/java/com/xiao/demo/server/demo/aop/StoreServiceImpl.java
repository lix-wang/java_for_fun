package com.xiao.demo.server.demo.aop;

/**
 * @author wang lingxiao(lix.wang@alo7.com)
 */
public class StoreServiceImpl implements StoreService {
    @Override
    public void customerIn() {
        System.out.println(".....customer in.....");
    }

    @Override
    public void buy() {
        System.out.println(".....customer buy.....");
    }

    @Override
    public void customerOut() {
        System.out.println(".....customer out.....");
    }
}
