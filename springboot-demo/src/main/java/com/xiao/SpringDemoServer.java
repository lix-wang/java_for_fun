package com.xiao;

import com.xiao.framework.server.undertow.BaseUndertowApplication;

/**
 *
 * @author lix wang
 */
public class SpringDemoServer extends BaseUndertowApplication {
    public static void main(String[] args) {
        start(SpringDemoServer.class, args);
    }
}
