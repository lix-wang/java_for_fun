package com.xiao.framework.biz.environment;

import com.xiao.framework.biz.utils.ObjectHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lix wang
 */
public class EnvConfigProvider<T> {
    private final CmdLineConfig cmdLineConfig;
    private final T config;
    private final Class<T> clazz;

    public EnvConfigProvider(CmdLineConfig cmdLineConfig, T config, Class<T> clazz) {
        this.cmdLineConfig = cmdLineConfig;
        this.config = config;
        this.clazz = clazz;
    }

    public T getConfigBean() throws Exception {
        ProfileType profileType = cmdLineConfig.getProfile();
        setValueFromAnnotation(config, profileType);
        return config;
    }

    private void setValueFromAnnotation(Object object, ProfileType profile) throws Exception {
        for (Field field : clazz.getDeclaredFields()) {
            EnvConfig envConfig = getValidEnvConfig(field, profile);
            setFieldValue(object, field, getEnvConfigValue(envConfig));
        }
    }

    private EnvConfig getValidEnvConfig(Field field, ProfileType profile) {
        List<EnvConfig> result = new ArrayList<>();
        EnvConfig[] annotations = field.getAnnotationsByType(EnvConfig.class);
        for (EnvConfig annotation : annotations) {
            if (ArrayUtils.contains(annotation.environments(), profile)) {
                result.add(annotation);
            }
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        return null;
    }

    private String getEnvConfigValue(EnvConfig annotation) {
        if (annotation.encrypted()) {
            return StringUtils.isBlank(annotation.value()) ? annotation.value()
                    : decrptyValue(annotation.value(), EnvConfig.DECRYPT_KEY_PROPERTY);
        } else {
            return annotation.value();
        }
    }

    private void setFieldValue(Object object, Field field, String value) throws Exception {
        field.setAccessible(true);
        field.set(object, ObjectHelper.getRealValue(field.getType(), value));
    }

    private String decrptyValue(@NotNull String value, @NotNull String decryptKey) {
        // todo decrypt value
        return null;
    }
}
