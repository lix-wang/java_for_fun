package com.xiao.framework.demo;

import org.apache.commons.lang3.concurrent.Computable;

import java.util.HashMap;
import java.util.Map;

/**
 * An efficient cache written by HashMap and synchronized
 *
 * @author lix wang
 */
public class Memoizer<I, O> implements Computable<I, O> {
    private final Map<I, O> cache = new HashMap<>();
    private final Computable<I, O> c;

    public Memoizer(Computable<I, O> c) {
        this.c = c;
    }

    @Override
    public synchronized O compute(I i) throws InterruptedException {
        O result = cache.get(i);
        if (result == null) {
            result = c.compute(i);
            cache.put(i, result);
        }
        return result;
    }
}
