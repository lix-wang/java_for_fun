package com.xiao.framework.task;

/**
 *
 * @author lix wang
 */
@FunctionalInterface
public interface TaskPoller<T> {
    T getTask();
}
