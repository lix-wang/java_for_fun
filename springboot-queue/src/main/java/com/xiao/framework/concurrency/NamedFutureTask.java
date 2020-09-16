package com.xiao.framework.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Named future task.
 *
 * @author lix wang
 */
public class NamedFutureTask<V> extends FutureTask<V> {
    private final String name;

    public NamedFutureTask(String name, Callable<V> callable) {
        super(callable);
        this.name = name;
    }

    public NamedFutureTask(String name, Runnable runnable, V result) {
        super(runnable, result);
        this.name = name;
    }

    @Override
    public String toString() {
        return "NamedFutureTask{" +
                "name='" + name + '\'' +
                '}';
    }
}
