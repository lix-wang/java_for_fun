package com.xiao.framework.pool;

import com.xiao.framework.concurrency.LinkedTaskQueue;
import com.xiao.framework.concurrency.TaskExecutor;

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

    private static final ThreadPoolExecutor DEFAULT_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, new LinkedBlockingQueue());

    private static final TaskExecutor DEFAULT_TASK_EXECUTOR = new TaskExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
            0, TIME_UNIT, new LinkedTaskQueue<>());

    public static ThreadPoolExecutor pool() {
        return DEFAULT_EXECUTOR;
    }

    public static TaskExecutor taskPool() {
        return DEFAULT_TASK_EXECUTOR;
    }
}
