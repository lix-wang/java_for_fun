package com.xiao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author lix wang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String name;
    private Date birthday;
    // should be 'M' or 'F'
    private String sex;
    private String mobilePhone;
    private String email;
    private String state;
    private String uuid;
    private String emailState;
    private String mobilePhoneState;
    private Date createTime;
    private Date updateTime;
}
