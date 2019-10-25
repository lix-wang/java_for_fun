package com.xiao.framework.demo;

import org.apache.commons.lang3.concurrent.Computable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * An efficient cache written by ConcurrentHashMap and FutureTask.
 * @author lix wang
 */
public class Memoizer3<I, O> implements Computable<I, O> {
    private final Map<I, Future<O>> cache = new ConcurrentHashMap<>();
    private final Computable<I, O> c;

    public Memoizer3(Computable<I, O> c) {
        this.c = c;
    }

    @Override
    public O compute(I i) throws InterruptedException {
        Future<O> future = cache.get(i);
        if (future == null) {
            FutureTask<O> futureTask = new FutureTask<>(() -> c.compute(i));
            cache.put(i, futureTask);
            futureTask.run();
        }
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
