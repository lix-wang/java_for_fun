package com.xiao.framework.demo;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 无法保证在同时更新上下界时还能保证不变性条件，因此通过对指向不可变对象的引用进行原子更新以避免竞态条件。
 *
 * @author lix wang
 */
public class CasNumberRange {
    private final AtomicReference<IntPair> values = new AtomicReference<>(new IntPair(0, 0));

    public int getLower() {
        return values.get().getLower();
    }

    public int getUpper() {
        return values.get().getUpper();
    }

    public void setLower(int i) {
        while (true) {
            IntPair oldValue = values.get();
            if (i > oldValue.upper) {
                throw new IllegalArgumentException("Can't set lower to " + i + " > upper");
            }
            IntPair newValue = new IntPair(i, oldValue.upper);
            if (values.compareAndSet(oldValue, newValue)) {
                return;
            }
        }
    }

    public void setUpper(int i) {
        while (true) {
            IntPair oldValue = values.get();
            if (i < oldValue.lower) {
                throw new IllegalArgumentException("Can't set upper to " + i + " < lower");
            }
            IntPair newValue = new IntPair(oldValue.lower, i);
            if (values.compareAndSet(oldValue, newValue)) {
                return;
            }
        }
    }

    /**
     * 不变性条件 lower <= upper
     */
    private static class IntPair {
        final int lower;
        final int upper;

        private IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        public int getLower() {
            return lower;
        }

        public int getUpper() {
            return upper;
        }
    }
}
