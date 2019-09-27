package com.xiao.framework.biz.converter;

import com.xiao.framework.biz.utils.JodaUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Converter to handle DateTime response.
 * Just for practice, actually useless.
 *
 * @author lix wang
 */
public class DateTimeResponseConverter extends AbstractHttpMessageConverter<DateTime> {
    @Override
    protected boolean supports(Class<?> clazz) {
        return DateTime.class.isAssignableFrom(clazz);
    }

    @Override
    protected DateTime readInternal(Class<? extends DateTime> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        String message = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
        return JodaUtils.getCSTDateTime(message);
    }

    @Override
    protected void writeInternal(DateTime dateTime, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(dateTime.toString().getBytes());
    }
}
