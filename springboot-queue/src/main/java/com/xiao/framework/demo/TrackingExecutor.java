package com.xiao.framework.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService which can track cancelled tasks.
 *
 * 应该将各个关闭操作串行执行，而不是并行执行，这样可以消除很多潜在的问题。
 * 并行的关闭操作可能导致关闭操作之间出现竞态条件或死锁。
 *
 * @author lix wang
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService executorService;
    private final Set<Runnable> tasksCancelledAtShutdown = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public List<Runnable> getCancelledTasks() {
        if (!executorService.isTerminated()) {
            throw new IllegalStateException("Executor is not terminated.");
        }
        return new ArrayList<>(tasksCancelledAtShutdown);
    }

    @Override
    public void execute(final Runnable runnable) {
        executorService.execute(() -> {
            try {
                runnable.run();
            } finally {
                if (isShutdown() && Thread.currentThread().isInterrupted()) {
                    tasksCancelledAtShutdown.add(runnable);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }
}
