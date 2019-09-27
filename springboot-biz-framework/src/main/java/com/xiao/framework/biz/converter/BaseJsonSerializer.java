package com.xiao.framework.biz.converter;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.xiao.framework.base.generic.TypeReference;

/**
 * Base JsonSerializer.
 *
 * @author lix wang
 */
public abstract class BaseJsonSerializer<T> extends JsonSerializer<T> implements TypeReference<T> {
    @Override
    public Class<T> handledType() {
        return getRawTypeClass();
    }
}
