package com.xiao.framework.thread.demo;

import org.apache.commons.lang3.concurrent.Computable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An efficient cache written by ConcurrentHashMap.
 *
 * @author lix wang
 */
public class Memoizer2<I, O> implements Computable<I, O> {
    private final Map<I, O> cache = new ConcurrentHashMap<>();
    private final Computable<I, O> c;

    public Memoizer2(Computable<I, O> c) {
        this.c = c;
    }

    @Override
    public O compute(I i) throws InterruptedException {
        O result = cache.get(i);
        if (result == null) {
            result = c.compute(i);
            cache.put(i, result);
        }
        return result;
    }
}
