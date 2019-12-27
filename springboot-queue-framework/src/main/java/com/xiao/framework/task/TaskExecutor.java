package com.xiao.framework.task;

import com.xiao.framework.base.utils.Assert;
import com.xiao.framework.queue.TaskQueue;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lix wang
 */
public class TaskExecutor {
    private final TaskQueue<Runnable> taskQueue;
    private final ThreadFactory threadFactory;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final ExecutorState executorState = new ExecutorState();
    private volatile boolean allowCoreTimeout = true;
    private AtomicInteger workerCount = new AtomicInteger(0);

    private static final ReentrantLock lock = new ReentrantLock();
    private final HashSet<TaskWorker> workers = new HashSet<>();
    private static final ReentrantLock workerLock = new ReentrantLock();

    public TaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit,
            TaskQueue<Runnable> taskQueue) {
        Assert.check(taskQueue != null, "TaskExecutor executor and taskQueue can't be null");
        Assert.check(maximumPoolSize >= corePoolSize,
                "maximumPoolSize can't be less than corePoolSize");
        this.taskQueue = taskQueue;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.threadFactory = TaskThreadFactory.newInstance();
    }

    public <T> Future<T> submit(Callable<T> task) {
        Assert.check(task != null, "TakExecutor submit task can't be null");
        FutureTask<T> futureTask = new FutureTask<>(task);
        taskQueue.add(futureTask);
        addWorker();
        return futureTask;
    }

    public Future<?> submit(Runnable task) {
        Assert.check(task != null, "TakExecutor submit task can't be null");
        FutureTask<Void> futureTask = new FutureTask(task, null);
        taskQueue.add(futureTask);
        addWorker();
        return futureTask;
    }

    public boolean cancel(Object object) {
        return taskQueue.remove(object);
    }

    public void terminate() {
        this.executorState.stopped = true;
    }

    public void setAllowCoreTimeout(boolean allowCoreTimeout) {
        this.allowCoreTimeout = allowCoreTimeout;
    }

    /**
     * Simple but stupid design. Can use ThreadPoolExecutor.ctl instead.
     */
    static class ExecutorState {
        private volatile boolean stopped = false;
        private volatile boolean started = false;

        public boolean isStopped() {
            return stopped;
        }

        public boolean isStarted() {
            return started;
        }
    }

    private boolean addWorker() {
        boolean workerAdded = false;
        TaskWorker taskWorker = null;
        try {
            if (workerCount.get() >= maximumPoolSize) {
                return false;
            }
            if (executorState.isStopped() || taskQueue.isEmpty()) {
                return false;
            }
            lock.lock();
            // double check
            if (workerCount.get() >= maximumPoolSize) {
                return false;
            }
            taskWorker = new TaskWorker(threadFactory, defaultTaskPoller(), defaultTaskEndHandler(), executorState);
            Thread thread;
            if ((thread = taskWorker.getThread()) != null) {
                workers.add(taskWorker);
                // trible check
                if (workers.size() <= maximumPoolSize) {
                    thread.start();
                    workerAdded = true;
                    workerCount.getAndIncrement();
                }
            }
        } finally {
            if (!workerAdded) {
                removeWorker(taskWorker);
            }
            lock.unlock();
        }
        return workerAdded;
    }

    private void removeWorker(TaskWorker taskWorker) {
        if (taskWorker == null) {
            return;
        }
        lock.lock();
        try {
            workers.remove(taskWorker);
            workerCount.getAndDecrement();
        } finally {
            lock.unlock();
        }
    }

    private TaskPoller<Runnable> defaultTaskPoller() {
        return () -> {
            final long nanos = timeUnit.toNanos(keepAliveTime);
            long deadline = nanos + System.nanoTime();
            for(;;) {
                if (executorState.isStopped()) {
                    return null;
                }
                boolean needTimeout = checkTimeoutFlag();
                Runnable task = taskQueue.take();
                if (task != null) {
                    return task;
                } else if (needTimeout) {
                    deadline = deadline - System.nanoTime();
                    if (deadline <= 0L) {
                        return null;
                    }
                }
                LockSupport.parkNanos(this, nanos);
            }
        };
    }

    private TaskEndHandler defaultTaskEndHandler() {
        return (worker, completedAbruptly) -> {
            if (completedAbruptly) {
                workerCount.getAndDecrement();
            }
            workerLock.lock();
            try {
                workers.remove(worker);
            } finally {
                workerLock.unlock();
            }
            if (!executorState.isStopped()) {
                if (!completedAbruptly) {
                    int min = checkTimeoutFlag() ? 0 : corePoolSize;
                    if (min == 0 && !taskQueue.isEmpty()) {
                        min = 1;
                    }
                    if (workerCount.get() >= min) {
                        return;
                    }
                }
                addWorker();
            }
        };
    }

    private boolean checkTimeoutFlag() {
        return allowCoreTimeout || workerCount.get() > corePoolSize;
    }
}
