package com.xiao.service.impl;

import com.xiao.mapper.common.UserMapper;
import com.xiao.model.User;
import com.xiao.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 *
 * @author lix wang
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SessionServiceImpl implements SessionService {
    private final UserMapper userMapper;

    @Override
    public User getBySession(@NotNull HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization) && StringUtils.isNumeric(authorization)) {
            return userMapper.getById(Long.valueOf(authorization));
        }
        return null;
    }
}
