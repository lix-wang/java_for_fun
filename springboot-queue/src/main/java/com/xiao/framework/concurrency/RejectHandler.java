package com.xiao.framework.concurrency;

/**
 * ThreadPool executor task reject handler.
 *
 * @author lix wang
 */
@FunctionalInterface
public interface RejectHandler {
    void reject(Runnable task, TaskExecutor executor);
}
