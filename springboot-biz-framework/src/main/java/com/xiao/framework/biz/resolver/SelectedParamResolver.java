package com.xiao.framework.biz.resolver;

import com.xiao.framework.biz.utils.ObjectHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;

/**
 * Resolver for {@link SelectedRequestParam}
 *
 * @author lix wang
 */
public class SelectedParamResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SelectedRequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        SelectedRequestParam annotation = parameter.getParameterAnnotation(SelectedRequestParam.class);
        String realValue = null;
        if (annotation != null) {
            List<String> expectedValues = Arrays.asList(annotation.defaultValue());
            String value = webRequest.getParameter(annotation.name());
            if (StringUtils.isEmpty(value)) {
                if (annotation.required()) {
                    throw new MissingServletRequestParameterException(annotation.name(), "String");
                } else {
                    if (!annotation.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                        realValue = annotation.defaultValue();
                    }
                }
            } else {
                if (expectedValues.contains(value)) {
                    realValue = value;
                } else {
                    throw new RuntimeException(String.format("Parameter %s is not expected value.", annotation.name()));
                }
            }
        }
        return ObjectHelper.getRealValue(parameter.getParameterType(), realValue);
    }
}
