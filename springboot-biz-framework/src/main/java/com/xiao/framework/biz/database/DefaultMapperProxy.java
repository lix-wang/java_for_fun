package com.xiao.framework.biz.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author lix wang
 */
public class DefaultMapperProxy<T> implements InvocationHandler {
    private static final Logger logger = LogManager.getLogger(DefaultMapperProxy.class);
    private Class<T> realMapperClazz;
    private final T realMapper;

    public DefaultMapperProxy(Class<T> realMapperClazz, T realMapper) {
        this.realMapperClazz = realMapperClazz;
        this.realMapper = realMapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = method.invoke(realMapper, args);
        logger.info("Execute " + realMapperClazz.getSimpleName() + "." + method.getName() + " consume: "
                + (System.currentTimeMillis() - start) + " ms");
        return result;
    }
}
