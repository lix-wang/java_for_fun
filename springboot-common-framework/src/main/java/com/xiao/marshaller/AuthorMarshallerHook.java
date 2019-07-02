package com.xiao.marshaller;

import com.xiao.framework.biz.marshaller.MarshallerHook;
import lombok.extern.log4j.Log4j2;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Marshaller hook of Author.
 *
 * @author lix wang
 */
@Log4j2
@Component
public class AuthorMarshallerHook implements MarshallerHook {
    @Override
    public void beforeMarshaller(Object graph) {
        log.info("Before marshal author: " + graph.toString());
    }

    @Override
    public void afterMarshaller(Result result) {
        log.info("After marshal author, result is: " + result.toString());
    }

    @Override
    public void onMarshallerXmlMappingException(XmlMappingException xmlMappingException) {
        log.error("Marshal author failed: " + xmlMappingException, xmlMappingException);
    }

    @Override
    public void beforeUnmarshaller(Source source) {
        log.info("Before unmarshal author: " + source.toString());
    }

    @Override
    public void afterUnmarshaller(Object object) {
        log.info("After unmarshal author: " + object.toString());
    }

    @Override
    public void onUnmarshallerXmlMappingException(XmlMappingException xmlMappingException) {
        log.error("Unmarshal author failed: " + xmlMappingException, xmlMappingException);
    }
}
