package com.xiao.biz.database;

import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


/**
 * Factory to create Mapper Beans.
 *
 * @author lix wang
 */
public class LixMapperFactoryBean<T> extends MapperFactoryBean<T> {
    public LixMapperFactoryBean(Class<T> targetInterface) {
        super(targetInterface);
    }

    @Override
    public T getObject() throws Exception {
        T realMapper = super.getObject();
        InvocationHandler handler = new DefaultMapperProxy<>(getObjectType(), realMapper);
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{getObjectType()}, handler);
    }
}
