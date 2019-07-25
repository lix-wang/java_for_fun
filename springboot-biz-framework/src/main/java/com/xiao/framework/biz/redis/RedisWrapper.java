package com.xiao.framework.biz.redis;

import lombok.Builder;
import lombok.Data;

/**
 * Wrapper for redis slave.
 *
 * @author lix wang
 */
@Data
@Builder
public class RedisWrapper {
    private String host;
    private int port;
    private String password;
}
