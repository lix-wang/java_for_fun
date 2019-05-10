package com.xiao.database;

import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author lix wang
 */
public class DefaultMapperProxy<T> implements InvocationHandler {
    private Class<T> realMapperClazz;
    private final T realMapper;
    private final Logger logger;

    public DefaultMapperProxy(Class<T> realMapperClazz, T realMapper, Logger logger) {
        this.realMapperClazz = realMapperClazz;
        this.realMapper = realMapper;
        this.logger = logger;
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
