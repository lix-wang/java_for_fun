package com.xiao.controller;

import com.xiao.config.DemoConfig;
import com.xiao.framework.biz.resolver.SelectedRequestParam;
import com.xiao.framework.rpc.async.AsyncCall;
import com.xiao.framework.rpc.http.BaseHttpCall;
import com.xiao.framework.rpc.http.HttpCall;
import com.xiao.framework.rpc.http.HttpCallFactory;
import com.xiao.framework.rpc.http.HttpRequestWrapper;
import com.xiao.framework.rpc.model.AbstractAsyncResult;
import com.xiao.mapper.common.UserMapper;
import com.xiao.model.response.DemoConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author lix wang
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestController {
    private final DemoConfig config;
    private final UserMapper userMapper;

    @GetMapping("/getDemoConfig")
    public DemoConfigResponse getDemoConfig() {
        return DemoConfigResponse.builder().value(config.getDemoField()).user(userMapper.getById(2L)).build();
    }

    @GetMapping("/getSelectedParam")
    public long getSelectedParam(
            @SelectedRequestParam(name = "paramNum", required = false, defaultValue = "5", expectedValue = {"1", "2"})
                    long paramNum) {
        return paramNum;
    }

    @GetMapping("/testHttpRequest")
    public String testHttpRequest() {
        String result;
        HttpRequestWrapper request = HttpRequestWrapper.builder().url("https://www.baidu.com").build();
        AbstractAsyncResult<Response> response;
        try {
            response = AsyncCall.callAsync(
                    () -> HttpCallFactory.get(new BaseHttpCall(), HttpCall.class).asyncCallWithOkHttp(request));
            log.info("I already start a async http request");
            result = response.get().body().string();
            log.info("I got a response " + result);
        } catch (Exception e) {
            log.error("Async request http failed " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return result;
    }
}
