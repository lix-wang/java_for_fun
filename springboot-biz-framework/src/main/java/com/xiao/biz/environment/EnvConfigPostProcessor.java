package com.xiao.biz.environment;

import com.xiao.biz.utils.ObjectHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author lix wang
 */
@Log4j2
@Component
public class EnvConfigPostProcessor implements BeanDefinitionRegistryPostProcessor {
    /**
     * 在标准初始化完成后，可以修改容器内部的Bean Definition Registry。所有的常规的Bean将被加载，但是没有被实例化，这允许我们在下一个
     * 处理阶段增加一些新的Bean定义
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 获取所有自定义加载的Bean names
        List<String> beanNames = new ArrayList<>();
        Arrays.stream(registry.getBeanDefinitionNames()).forEach(beanName -> {
            // 获取BeanDefinition
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                String className = getClassName((AnnotatedBeanDefinition) beanDefinition);
                Class clazz = ObjectHelper.getClassByClassName(className);
                // 如果该Class被特定的注解注释，则进行处理
                if (clazz != null && clazz.isAnnotationPresent(LixConfiguration.class)) {
                    GenericBeanDefinition newBeanDefinition = new GenericBeanDefinition();
                    newBeanDefinition.setBeanClass(EnvConfigBeanFactory.class);
                    newBeanDefinition.setPrimary(true);
                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    constructorArgumentValues.addGenericArgumentValue(className);
                    newBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
                    registry.removeBeanDefinition(beanName);
                    registry.registerBeanDefinition(beanName, newBeanDefinition);
                    beanNames.add(beanName);
                }
            }
        });
        log.info("Found custom configurations: " + beanNames);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("Post processor do post process bean factory");
    }

    private String getClassName(AnnotatedBeanDefinition annotatedBeanDefinition) {
        String className = null;
        if (annotatedBeanDefinition == null) {
            return className;
        }
        // 先判断是否已经产生Bean，如果产生则根据Bean获取className，否则根据Class<?> 获取className
        if (annotatedBeanDefinition.getFactoryMethodMetadata() != null
                && annotatedBeanDefinition.getFactoryMethodMetadata().getReturnTypeName() != null) {
            className = annotatedBeanDefinition.getFactoryMethodMetadata().getReturnTypeName();
        } else {
            className = annotatedBeanDefinition.getMetadata().getClassName();
        }
        return className;
    }
}
