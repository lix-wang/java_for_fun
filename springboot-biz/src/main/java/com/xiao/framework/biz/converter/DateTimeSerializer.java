package com.xiao.framework.biz.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * DateTime jackson serializer.
 *
 * @author lix wang
 */
@Component
public class DateTimeSerializer extends BaseJsonSerializer<DateTime> {
    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
