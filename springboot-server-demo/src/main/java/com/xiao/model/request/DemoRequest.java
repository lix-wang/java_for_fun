package com.xiao.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DemoRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String description;
}
