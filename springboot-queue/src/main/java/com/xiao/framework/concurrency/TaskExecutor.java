package com.xiao.framework.concurrency;

import com.xiao.framework.base.exception.LixRuntimeException;
import com.xiao.framework.base.exception.LixStatusCode;
import com.xiao.framework.base.utils.Assert;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final RejectHandler rejectHandler;

    private volatile boolean allowCoreTimeout = true;
    private volatile int state = RUNNING;

    // 运行中，可以接收新任务执行任务队列中的任务
    private static final int RUNNING = -1;
    // 不再接受新任务，会执行任务队列中的任务。
    private static final int SHUTDOWN = 0;
    // 不再接受新任务，不会执行任务队列中的任务，中断所有执行中的任务。
    private static final int STOP = 1;
    // 整理中状态，所有任务已经终结，工作线程数为0，过渡到改状态的工作线程会调用terminated()
    private static final int TIDYING = 2;
    // 终结状态，terminated() 执行完毕。
    private static final int TERMINATED = 3;

    private static final int CAPACITY = Integer.MAX_VALUE;
    private static final Object LOCK = new Object();

    volatile long completedTaskNum;

    private final HashSet<Worker> workers = new HashSet<>();
    private final AtomicInteger workerCount = new AtomicInteger(0);
    private static final RejectHandler defaultRejectHandler = (task, executor) -> {
        throw LixRuntimeException.builder()
                .statusCode(LixStatusCode.FORBIDDEN)
                .message("Task " + task.toString() + " rejected by " + executor.toString())
                .build();
    };

    public TaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit,
            TaskQueue<Runnable> taskQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, taskQueue, defaultRejectHandler);
    }

    public TaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit,
            TaskQueue<Runnable> taskQueue, RejectHandler rejectHandler) {
        Assert.check(taskQueue != null, "TaskExecutor executor and taskQueue can't be null");
        Assert.check(maximumPoolSize >= corePoolSize,
                "maximumPoolSize can't be less than corePoolSize");
        this.taskQueue = taskQueue;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.threadFactory = TaskThreadFactory.newInstance();
        this.rejectHandler = rejectHandler;
    }

    public <T> Future<T> submit(Callable<T> task) {
        return submit(null, task);
    }

    public Future<?> submit(Runnable task) {
        return submit(null, task);
    }

    public <T> Future<T> submit(String name, Callable<T> task) {
        Assert.check(task != null, "TakExecutor submit task can't be null");
        FutureTask<T> futureTask = new NamedFutureTask<>(name, task);
        executeTask(futureTask);
        return futureTask;
    }

    public Future<?> submit(String name, Runnable task) {
        Assert.check(task != null, "TakExecutor submit task can't be null");
        FutureTask<Void> futureTask = new NamedFutureTask<>(name, task, null);
        executeTask(futureTask);
        return futureTask;
    }

    /**
     * 处理任务。
     */
    private void executeTask(@NotNull Runnable runnable) {
        // 如果当前工作线程数少于核心线程数，那么需要创建新的工作线程。此时创建的线程是核心线程。
        if (workerCount.get() < corePoolSize) {
            if (createWorker(runnable, true)) {
                return;
            }
        }
        // 走到这一步说明可能情况如下：1，工作线程不少于核心线程数。2，创建新工作线程失败。
        // 创建核心线程失败，需要进行如下操作：
        // 1.如果state <= RUNNING，那么尝试提交任务到taskQueue。
        // 2.如果提交失败，可能是任务队列满了，此时尝试增加非核心工作线程，如果增加工作线程失败，那么拒绝当前任务的提交。
        // 3.如果提交成功，检查运行状态，如果 >= SHUTDOWN，那么需要移除任务，如果成功移除拒绝任务，无法移除，说明任务已经完成。
        // 4.如果提交成功，并且处于运行状态，那么需要检查工作线程数，如果工作线程数 == 0，那么要创建一个工作线程用以处理任务。
        // 这一步，需要二次检查运行状态，并需要检查工作线程数，因为如果不检查，那么程序认为任务提交成功，但是可能永远无法执行该任务。
        if (isRunning() && enqueueTask(runnable)) {
            if (!isRunning() && removeTask(runnable)) {
                reject(runnable);
            } else if (workerCount.get() == 0) {
                createWorker(null, false);
            }
        } else if (!createWorker(runnable, false)) {
            reject(runnable);
        }
    }


    /**
     *  创建工作线程
     *
     * @param headTask 该工作线程的首任务。
     * @param core 是否为核心线程。
     */
    private boolean createWorker(Runnable headTask, boolean core) {
        // 采用循环重试机制
        while (true) {
            // 如果存在以下任一条件，则无法创建新工作线程:
            // 1.状态 > SHUTDOWN。
            // 2.状态 == SHUTDOWN 并且 headTask != null。因为此状态不能接受新任务。
            // 3.状态 == SHUTDOWN 并且任务队列为空。
            if ((state >= SHUTDOWN) && (state >= STOP || headTask != null || taskQueue.isEmpty())) {
                return false;
            }
            // 如果线程池状态满足，但是存在以下任一条件，那么依然不能创建工作线程：
            // 1.任务数超过CAPACITY。
            // 2.如果创建的是核心线程，那么核心线程数不能超过corePoolSize，如果是工作线程，那么工作线程数不能超过maximumPoolSize。
            int currentWorkerCount = workerCount.get();
            if (currentWorkerCount >= CAPACITY || currentWorkerCount >= (core? corePoolSize : maximumPoolSize)) {
                return false;
            }
            // 利用cas更新workerCount，如果失败说明有其他线程进行了更改。
            if (compareAndIncreaseWorkerCount()) {
                break;
            }
        }
        boolean added = false;
        boolean started = false;
        Worker worker = null;
        try {
            // 更新workerCount成功，这时需要创建真正的工作线程。
            worker = new Worker(headTask);
            final Thread thread = worker.thread;
            if (thread != null) {
                // 这里需要锁住，因为需要保证独占性。能用synchronized的情况下，尽量不用ReentrantLock。
                synchronized (LOCK) {
                    // 如果此时线程池满足以下条件，那么可以将该线程加入工作线程中:
                    // 1.状态 < SHUTDOWN。
                    // 2.状态 == SHUTDOWN && headTask == null
                    if (state < SHUTDOWN || (state == SHUTDOWN && headTask == null)) {
                        // 若线程已经启动，那么属于异常情况。
                        if (thread.isAlive()) {
                            throw new IllegalThreadStateException();
                        }
                        workers.add(worker);
                        added = true;
                    }
                }
                // 如果添加成功，那么需要启动这个工作线程，这一步不需要独占，故放在同步代码块外部。
                if (added) {
                    thread.start();
                    started = true;
                }
            }
        } finally {
            // 如果started == false 表示未成功创建并启动工作线程。
            if (!started) {
                // 从工作线程集合中移除当前worker。
                removeFiledWorker(worker);
            }
        }
        return added;
    }

    /**
     * 移除创建失败的工作线程，这里使用公用的LOCK，也可以使用一个新的锁，降低阻塞的可能性。
     *
     * @param worker
     */
    private void removeFiledWorker(Worker worker) {
        if (worker != null) {
            synchronized (LOCK) {
                // 从工作线程集中，删除当前线程。
                workers.remove(worker);
                decreaseWorkerCount();
            }
        }
    }

    private boolean enqueueTask(@NotNull Runnable command) {
        return taskQueue.add(command);
    }

    private boolean removeTask(@NotNull Runnable command) {
        return taskQueue.remove(command);
    }

    private boolean isRunning() {
        return state < SHUTDOWN;
    }

    private void reject(@NotNull Runnable command) {
        rejectHandler.reject(command, this);
    }

    private void processWorkerExit(Worker worker, boolean completeAbrupt) {
        // 如果是异常退出，那么首先需要减少工作线程数。
        if (completeAbrupt) {
            decreaseWorkerCount();
        }
        synchronized (LOCK) {
            completedTaskNum += worker.completedTaskNum;
            workers.remove(worker);
        }
        // 如果state < STOP ，并且是异常退出，那么直接重新创建worker，如果是正常退出，那么如果退出后没有可用的worker了，
        // 并且任务队列不为空，此时也要重新创建worker。
        if (state <= SHUTDOWN) {
            if (!completeAbrupt) {
                // 找出工作线程数最小值。
                int min = allowCoreTimeout ? 0 : corePoolSize;
                // 如果min是0，当前workerCount == 1， 那么如果taskQueue不为空，就需要重建worker。
                if (min == 0 && !taskQueue.isEmpty()) {
                    min = 1;
                }
                if (workerCount.get() >= min) {
                    return;
                }
            }
            createWorker(null, false);
        }
    }

    private void decreaseWorkerCount() {
        int currentWorkerCount;
        // 减少工作线程数量
        do {
            currentWorkerCount = workerCount.get();
        } while (!workerCount.compareAndSet(currentWorkerCount, currentWorkerCount - 1));
    }

    private boolean compareAndIncreaseWorkerCount() {
        int currentWorkerCount = workerCount.get();
        return workerCount.compareAndSet(currentWorkerCount, currentWorkerCount + 1);
    }

    private Runnable getTask() {
        boolean timeout = false;
        while (true) {
            // 如果线程池 state >= STOP 或者线程池 state == SHUTDOWN && taskQueue.isEmpty。返回null减少工作线程数。
            if ((state >= SHUTDOWN) && (state >= STOP || taskQueue.isEmpty())) {
                decreaseWorkerCount();
                return null;
            }
            boolean allowTimeout = checkTimeoutFlag();
            int currentWorkerCount = workerCount.get();
            // 如果当前线程数大于最大线程数，或者允许超时，并且已经超时。而且当前工作线程数大于 1 或者任务队列为空。
            // 此时允许该线程退出。
            if((currentWorkerCount > maximumPoolSize || (allowTimeout && timeout))
                    && (currentWorkerCount > 1 || taskQueue.isEmpty())) {
                // 减少线程数，退出。
                decreaseWorkerCount();
                return null;
            }
            // 根据是否允许过期，选择不同的获取任务的方式。
            try {
                Runnable task = allowTimeout ? taskQueue.poll(keepAliveTime, timeUnit) : taskQueue.take();
                if (task != null) {
                    return task;
                }
                timeout = true;
            } catch (InterruptedException retry) {
                timeout = false;
            }
        }
    }

    private boolean checkTimeoutFlag() {
        return allowCoreTimeout || workerCount.get() > corePoolSize;
    }

    private class Worker implements Runnable {
        private final Thread thread;
        private Runnable headTask;
        private long completedTaskNum;

        private Worker(Runnable headTask) {
            this.thread = threadFactory.newThread(this);
            this.headTask = headTask;
        }

        @Override
        public void run() {
            Runnable task = headTask;
            boolean completeAbrupt = true;
            String taskName = null;
            try {
                while (task != null || (task = getTask()) != null) {
                     taskName = task.toString();
                    // 如果线程池状态 >= STOP 或者当前线程被中断，并且此时线程池状态 >= STOP，且此时线程未中断，那么需要确保中断。
                    if ((state >= STOP || (Thread.interrupted() && state >= STOP)) && !thread.isInterrupted()) {
                        thread.interrupt();
                    }
                    try {
                        task.run();
                    } finally {
                        task = null;
                        completedTaskNum++;
                    }
                }
                completeAbrupt = false;
            } finally {
                processWorkerExit(this, completeAbrupt);
            }
        }
    }
}
