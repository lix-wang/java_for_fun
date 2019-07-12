package com.xiao.framework.rpc.http;

import com.xiao.framework.rpc.proxy.AsyncHttpCallProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lix wang
 */
public class HttpCallFactory {
    private static Map<String, Object> HTTP_PROXIES = new ConcurrentHashMap<>();

    public static <T> T get(T target, Class<T> targetType) {
        return (T) getHttpCallProxy(target, targetType);
    }

    public static <T> T create(T target, Class<T> targetType) {
        return (T) createHttpCallProxy(target, targetType);
    }

    private static <T> Object getHttpCallProxy(T target, Class<T> targetType) {
        String proxyName = computeProxyName(target, targetType);
        if (!HTTP_PROXIES.containsKey(proxyName)) {
            HTTP_PROXIES.put(proxyName, createHttpCallProxy(target, targetType));
        }
        return HTTP_PROXIES.get(proxyName);
    }

    private static <T> Object createHttpCallProxy(T target, Class<T> targetType) {
        InvocationHandler handler = new AsyncHttpCallProxy<>(targetType, target);
        return Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                new Class[]{targetType}, handler);
    }

    private static <T> String computeProxyName(T target, Class<T> targetType) {
        return "Http_Call_Proxy_" + target.getClass().getName() + "_" + targetType.getName();
    }
}
