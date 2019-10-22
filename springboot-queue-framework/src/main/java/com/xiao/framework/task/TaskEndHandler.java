package com.xiao.framework.task;

/**
 *
 * @author lix wang
 */
@FunctionalInterface
public interface TaskEndHandler {
    void process(TaskWorker taskWorker, boolean completedAbruptly);
}
