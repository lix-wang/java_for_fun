package com.xiao.framework.biz.converter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

import java.util.Collection;

/**
 * Configuration for jackson objectMapper.
 *
 * @author lix wang
 */
@Configuration
public class JacksonObjectMapperConfiguration {
    @Bean
    public ObjectMapper createCustomObjectMapper(ApplicationContext applicationContext) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置包只含非null字段。
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        // 忽略无法识别的字段。
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(registerModule(applicationContext));
        return objectMapper;
    }

    private SimpleModule registerModule(@NotNull ApplicationContext applicationContext) {
        Collection<BaseJsonSerializer> serializers = applicationContext.getBeansOfType(BaseJsonSerializer.class)
                .values();
        Collection<BaseJsonDeserializer> deserializers = applicationContext.getBeansOfType(BaseJsonDeserializer.class)
                .values();
        SimpleModule simpleModule = new SimpleModule();
        if (CollectionUtils.isNotEmpty(serializers)) {
            serializers.forEach(serializer -> simpleModule.addSerializer(serializer));
        }
        if (CollectionUtils.isNotEmpty(deserializers)) {
            deserializers.forEach(deserializer -> simpleModule.addDeserializer(deserializer.getRawTypeClass(),
                    deserializer));
        }
        return simpleModule;
    }
}
