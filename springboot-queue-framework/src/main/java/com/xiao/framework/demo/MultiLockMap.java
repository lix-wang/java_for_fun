package com.xiao.framework.demo;

/**
 * 采用分段锁的机制，即使像clear方法会需要获取所有的锁，但是不需要同时获取所有锁。
 *
 * @author lix wang
 */
public class MultiLockMap {
    private static final int LOCK_NUM = 16;
    private final Node[] nodes;
    private final Object[] locks;

    public MultiLockMap(int numBuckets) {
        this.nodes = new Node[numBuckets];
        this.locks = new Object[LOCK_NUM];
        for (int i = 0; i < LOCK_NUM; i++) {
            locks[i] = new Object();
        }
    }

    private final int hash(Object key) {
        return Math.abs(key.hashCode() % nodes.length);
    }

    public Object get(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % LOCK_NUM]) {
            for (Node m = nodes[hash]; m != null; m = m.next) {
                if (m.key.equals(key)) {
                    return m.value;
                }
            }
        }
        return null;
    }

    public void clear() {
        for (int i = 0; i < nodes.length; i++) {
            synchronized (locks[i % LOCK_NUM]) {
                nodes[i] = null;
            }
        }
    }

    private static class Node {
        Object key;
        Object value;
        Node next;
    }
}
