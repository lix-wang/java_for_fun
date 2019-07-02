package com.xiao.marshaller;

import org.springframework.oxm.XmlMappingException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Hook for marshaller.
 *
 * @author lix wang
 */
public interface MarshallerHook {
    void beforeMarshaller(Object graph);
    void afterMarshaller(Result result);
    void onMarshallerXmlMappingException(XmlMappingException xmlMappingException);
    void beforeUnmarshaller(Source source);
    void afterUnmarshaller(Object object);
    void onUnmarshallerXmlMappingException(XmlMappingException xmlMappingException);
}
