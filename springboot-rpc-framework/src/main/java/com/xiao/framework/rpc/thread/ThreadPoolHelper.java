package com.xiao.framework.rpc.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Define a thread pool.
 *
 * @author lix wang
 */
public class ThreadPoolHelper {
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAX_POOL_SIZE = 20;
    private static final int KEEP_ALIVE_TIME = 0;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE_TIME, TIME_UNIT, new LinkedBlockingQueue());

    public static ThreadPoolExecutor getPool() {
        return executor;
    }
}
