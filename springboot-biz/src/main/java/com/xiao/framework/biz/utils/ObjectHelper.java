package com.xiao.framework.biz.utils;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lix wang
 */
@Log4j2
public class ObjectHelper {
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static Class getClassByClassName(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            log.debug("Get class failed.", e);
            return null;
        }
    }

    public static <T> T copyObject(Object target, Class<T> clazz) {
        T result = null;
        if (target != null) {
            result = copyObjects(ImmutableList.of(target), clazz).get(0);
        }
        return result;
    }

    public static <T> List<T> copyObjects(List<?> targets, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(targets)) {
            targets.forEach(target -> {
                try {
                    T response = clazz.newInstance();
                    BeanUtils.copyProperties(target, response);
                    result.add(response);
                } catch (Exception e) {
                    throw new RuntimeException("Console operationService convertToResponseList failed.", e);
                }
            });
        }
        return result;
    }

    public static Object getRealValue(Class<?> clazz, String value) {
        if (clazz.equals(boolean.class)) {
            return Boolean.valueOf(value);
        } else if (clazz.equals(int.class)) {
            return Integer.valueOf(value);
        } else if (clazz.equals(long.class)) {
            return Long.valueOf(value);
        } else if (clazz.equals(double.class)) {
            return Double.valueOf(value);
        } else if (clazz.equals(String.class)) {
            return value;
        } else {
            return null;
        }
    }
}
