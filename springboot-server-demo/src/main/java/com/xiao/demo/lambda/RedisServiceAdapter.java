package com.xiao.demo.lambda;

@FunctionalInterface
public interface RedisServiceAdapter {
    Boolean exists(String key);
}
