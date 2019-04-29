package com.xiao.controller;

import com.xiao.config.DemoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author lix wang
 */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestController {
    private final DemoConfig config;

    @GetMapping("/getDemoConfig")
    public String getDemoConfig() {
        return config.getDemoField();
    }
}
