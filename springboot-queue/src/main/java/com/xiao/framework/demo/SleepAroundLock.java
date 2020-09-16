package com.xiao.framework.demo;

/**
 * 利用轮询加休眠来实现阻塞。
 *
 * @author lix wang
 */
public class SleepAroundLock<V> {
    private static final long SLEEP_MILLS = 1000L;

    public void put(V value) throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isFull()) {
                    doPut(value);
                    return;
                }
            }
            Thread.sleep(SLEEP_MILLS);
        }
    }

    public V take() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isEmpty()) {
                    return doTake();
                }
            }
            Thread.sleep(SLEEP_MILLS);
        }
    }

    private V doTake() {
        return null;
    }

    private boolean isFull() {
        return true;
    }

    private void doPut(V value) {

    }

    private boolean isEmpty() {
        return false;
    }
}
