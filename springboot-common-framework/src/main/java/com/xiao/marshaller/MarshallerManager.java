package com.xiao.marshaller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Manager for marshaller.
 *
 * @author lix wang
 */
public class MarshallerManager {
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;
    @Getter
    @Setter
    private MarshallerHook marshallerHook;

    public MarshallerManager(Marshaller marshaller, Unmarshaller unmarshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    public void startMarshal(Object object, Result result) {
        Assert.notNull(marshaller, "Marshaller must not null.");
        try {
            onMarshallerStart(object);
            this.marshaller.marshal(object, result);
            onMarshallerEnd(result);
        } catch (Exception e) {
            onMarshallerException(e);
        }
    }

    public Object startUnmarshal(Source source) {
        Assert.notNull(unmarshaller, "Unmarshaller must not null.");
        Object result = null;
        try {
            onUnmarshallerStart(source);
            result = this.unmarshaller.unmarshal(source);
            onUnmarshallerEnd(result);
        } catch (Exception e) {
            onUnmarshallerException(e);
        }
        return result;
    }

    private void onMarshallerStart(Object object) {
        if (marshallerHook != null) {
            marshallerHook.beforeMarshaller(object);
        }
    }

    private void onMarshallerEnd(Result result) {
        if (marshallerHook != null) {
            marshallerHook.afterMarshaller(result);
        }
    }

    private void onMarshallerException(Exception ex) {
        if (marshallerHook != null && ex instanceof XmlMappingException) {
            marshallerHook.onMarshallerXmlMappingException((XmlMappingException) ex);
        } else {
            ex.printStackTrace();
        }
    }

    private void onUnmarshallerStart(Source source) {
        if (marshallerHook != null) {
            marshallerHook.beforeUnmarshaller(source);
        }
    }

    private void onUnmarshallerEnd(Object object) {
        if (marshallerHook != null) {
            marshallerHook.afterUnmarshaller(object);
        }
    }

    private void onUnmarshallerException(Exception ex) {
        if (marshallerHook != null && ex instanceof XmlMappingException) {
            marshallerHook.onUnmarshallerXmlMappingException((XmlMappingException) ex);
        } else {
            ex.printStackTrace();
        }
    }
}
