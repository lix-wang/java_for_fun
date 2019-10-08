package com.xiao.demo.server.demo.lambda;

@FunctionalInterface
public interface RedisServiceAdapter {
    Boolean exists(String key);
}
