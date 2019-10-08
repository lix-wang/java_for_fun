package com.xiao.demo.server.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DemoRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String description;
}
