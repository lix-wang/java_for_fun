package com.xiao.database;

import com.xiao.environment.CmdLineConfig;
import com.xiao.logging.LoggerFactoryService;
import com.xiao.logging.LoggerTypeEnum;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


/**
 * Factory to create Mapper Beans.
 *
 * @author lix wang
 */
public class LixMapperFactoryBean<T> extends MapperFactoryBean<T> implements ApplicationContextAware {
    private ApplicationContext context;

    public LixMapperFactoryBean(Class<T> targetInterface) {
        super(targetInterface);
    }

    @Override
    public T getObject() throws Exception {
        CmdLineConfig cmdLineConfig = context.getBean(CmdLineConfig.class);
        Logger logger = LoggerFactoryService.getLogger(LoggerTypeEnum.DEFAULT_LOGGER, cmdLineConfig.getProfile());
        T realMapper = super.getObject();
        InvocationHandler handler = new DefaultMapperProxy<>(getObjectType(), realMapper, logger);
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{getObjectType()}, handler);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
