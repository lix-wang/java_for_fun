package com.xiao.service;

import com.xiao.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 *
 * @author lix wang
 */
public interface SessionService {
    User getBySession(@NotNull HttpServletRequest request);
}
