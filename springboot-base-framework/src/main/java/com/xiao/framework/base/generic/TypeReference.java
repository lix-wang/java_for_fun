package com.xiao.framework.base.generic;

import com.xiao.framework.base.exception.LixException;
import com.xiao.framework.base.exception.LixStatusCode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Can be used to get superClassGenericType.
 *
 * @author lix wang
 */
public interface TypeReference<T> {
    default Class<T> getRawTypeClass() {
        Type type = getSuperClassGenericType();
        if (type instanceof Class) {
            return (Class<T>) type;
        }
        return null;
    }

    default Type getSuperClassGenericType() {
        return getSuperClassGenericType(getClass());
    }

    default Type getSuperClassGenericType(Class<?> clazz) {
        Type superClass = clazz.getGenericSuperclass();
        if (superClass instanceof Class) {
            if (TypeReference.class != superClass) {
                getSuperClassGenericType(clazz.getSuperclass());
            }
            throw LixException.builder()
                    .statusCode(LixStatusCode.FORBIDDEN)
                    .message("use TypeReference to get type of super class generic type failed")
                    .build()
                    .toRuntimeException();
        }
        Type result = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (result instanceof ParameterizedType) {
            result = ((ParameterizedType) result).getRawType();
        }
        return result;
    }
}
