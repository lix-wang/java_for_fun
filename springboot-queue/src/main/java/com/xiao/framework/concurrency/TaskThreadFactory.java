package com.xiao.framework.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author lix wang
 */
public class TaskThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public TaskThreadFactory(ThreadGroup group, String namePrefix) {
        this.group = group;
        this.namePrefix = namePrefix;
    }

    public static TaskThreadFactory newInstance() {
        ThreadGroup group = new ThreadGroup("TaskQueueThreadGroup");
        String namePrefix = "TaskQueue-thread-";
        return new TaskThreadFactory(group, namePrefix);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(this.group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
