package com.xiao.framework.biz.converter;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.xiao.framework.base.generic.TypeReference;

/**
 * Base JsonDeserializer.
 *
 * @author lix wang
 */
public abstract class BaseJsonDeserializer<T> extends JsonDeserializer<T> implements TypeReference {
}
