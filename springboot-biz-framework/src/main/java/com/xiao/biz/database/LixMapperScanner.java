package com.xiao.biz.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import javax.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Scanner to scan mapper interfaces.
 *
 * @author lix wang
 */
public class LixMapperScanner extends ClassPathBeanDefinitionScanner {
    @Getter
    @Setter
    private String sqlSessionTemplateName;

    public LixMapperScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    /**
     * Scan mapper interfaces as beanDefinitions.
     */
    public Set<BeanDefinitionHolder> doMapperScan(@NotNull String[] basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = new HashSet<>();
        Arrays.stream(basePackages).forEach(mapperPackage -> beanDefinitionHolders.addAll(doScan(basePackages)));
        // Handle mapper beanDefinition
        beanDefinitionHolders.forEach(beanDefinitionHolder -> {
            GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();

            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
            definition.setBeanClass(LixMapperFactoryBean.class);
            // Set DatabaseService by database
            definition.getPropertyValues().add("sqlSessionTemplate",
                    new RuntimeBeanReference(sqlSessionTemplateName));
        });
        return beanDefinitionHolders;
    }

    /**
     * According to ClassMetadataReadingVisitor#isConcrete(), it won't scan our target mapper interfaces.
     *
     * So we need override the method to let ClassPathBeanDefinitionScanner scan our target mapper interfaces.
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
