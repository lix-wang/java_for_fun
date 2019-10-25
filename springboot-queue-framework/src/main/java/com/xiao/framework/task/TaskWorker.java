package com.xiao.framework.task;

import com.xiao.framework.task.TaskExecutor.ExecutorState;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 *
 * @author lix wang
 */
final class TaskWorker extends AbstractQueuedSynchronizer implements Runnable {
    private final Thread thread;
    private final TaskPoller<Runnable> taskPoller;
    private final TaskEndHandler taskEndHandler;
    private final ExecutorState executorState;

    public TaskWorker(ThreadFactory threadFactory, TaskPoller<Runnable> taskPoller, TaskEndHandler taskEndHandler,
            ExecutorState executorState) {
        setState(-1);
        this.thread = threadFactory.newThread(this);
        this.taskPoller = taskPoller;
        this.taskEndHandler = taskEndHandler;
        this.executorState = executorState;
    }

    @Override
    public void run() {
        runWorker();
    }

    public boolean isLocked() {
        return isHeldExclusively();
    }

    private final void runWorker() {
        Runnable task;
        unlock();
        boolean completedAbruptly = true;
        try {
            while ((task = taskPoller.getTask()) != null) {
                lock();
                if (executorState.isStopped() && !thread.isInterrupted()) {
                    thread.interrupt();
                }
                try {
                    task.run();
                } finally {
                    unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            taskEndHandler.process(this, completedAbruptly);
        }
    }

    protected boolean tryRelease(int unused) {
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }

    protected boolean isHeldExclusively() {
        return getState() != 0;
    }

    protected boolean tryAcquire(int unused) {
        if (compareAndSetState(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    private void lock() {
        acquire(1);
    }

    private void unlock() {
        release(1);
    }

    public Thread getThread() {
        return thread;
    }
}
