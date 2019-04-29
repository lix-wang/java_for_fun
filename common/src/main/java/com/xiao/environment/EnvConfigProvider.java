package com.xiao.environment;

import org.apache.commons.lang3.ArrayUtils;

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
            return null;
        } else {
            return annotation.value();
        }
    }

    private void setFieldValue(Object object, Field field, String value) throws Exception {
        field.setAccessible(true);
        field.set(object, parseFieldValue(field, value));
    }

    private Object parseFieldValue(Field field, String value) {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(boolean.class)) {
            return Boolean.valueOf(value);
        } else if (fieldType.equals(int.class)) {
            return Integer.valueOf(value);
        } else if (fieldType.equals(long.class)) {
            return Long.valueOf(value);
        } else if (fieldType.equals(double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.equals(String.class)) {
            return value;
        } else {
            return null;
        }
    }
}
