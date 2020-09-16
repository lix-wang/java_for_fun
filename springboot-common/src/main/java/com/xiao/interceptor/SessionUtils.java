package com.xiao.interceptor;

import com.xiao.model.User;

/**
 *
 * @author lix wang
 */
public class SessionUtils {
    public static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();
}
