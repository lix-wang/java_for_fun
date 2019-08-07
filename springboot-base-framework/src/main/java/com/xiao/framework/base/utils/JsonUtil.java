package com.xiao.framework.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Json util of jackson.
 *
 * @author lix wang
 */
public class JsonUtil {
    private static final Logger logger = LogManager.getLogger(JsonUtil.class);
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    public static <T> T convertToObject(@NotNull InputStream inputStream, @NotNull Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            logger.error("Deserialize inputStream to object failed " + e.getMessage(), e);
        }
        return null;
    }

    public static <T extends Object> String serialize(T obj) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Serialize object failed " + e.getMessage(), e);
        }
        return null;
    }
}
