package com.xiao.framework.biz.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.xiao.framework.biz.utils.JodaUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * DateTime deserializer.
 *
 * @author lix wang
 */
@Component
public class DateTimeDeserializer extends BaseJsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String str = p.getValueAsString();
        return JodaUtils.getCSTDateTime(str);
    }
}
