package com.xiao.framework.base.collection;

import sun.misc.Contended;
import sun.misc.Unsafe;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * From {@link java.util.concurrent.ConcurrentHashMap}
 *
 * ConcurrentHashMap 同样采用数组存储Node，处理哈希冲突也是采用链表加红黑树的方式。
 * 在put时，发现哈希桶没有被初始化，那么会采用initTable来初始化。sizeCtl是一个用于同步多个线程的共享变量，如果这个值 < 0，
 * 那么说明当前哈希桶正在被初始化或者扩容，当线程发现sizeCtl < 0，就会让出CPU，当发现不再小于0时，会调用compareAndSwapInt，将sizeCtl
 * 变为 -1。我们发现在调用compareAndSwapInt方法前后，进行了两次校验table是否被初始化。因为，A线程初始化过程中，B线程也进行初始化，
 * 发现sizeCtl < 0 会等待，A初始化完成，如果B继续初始化，那么会有问题，所以要在前后进行两次初始化。
 *
 * ConcurrentHashMap只会对index位置上锁。在put时，会先查找index位置，如果为null，直接插入，否则上锁，判断是链表还是红黑树，
 * 使用对应的方法进行插入或更新。进行put之后，需要更新哈希桶的记录数量，更新后发现超过阀值，那么就需要扩容。
 *
 * 删除时，会对index位置进行上锁，然后删除。
 *
 * HashMap 允许一个key和value为null，ConcurrentHashMap不允许key和value为null，如果发现key或value为null，抛出NPE。
 *
 * ConcurrentHashMap快的主要原因，我认为是把锁加在桶上，而非整个对象上，这样一个桶上锁，并不影响其他的桶，另外读取是没有用锁，
 * 利用volatile可见性特点，使得每次桶中节点的修改都是即时可见的，这样可以避免锁带来的性能消耗。
 *
 * @author lix wang
 */
public class LixConcurrentHashMap<K, V> implements LixMap<K, V> {
    private static final int HASH_BITS = 0x7fffffff;
    private static final int DEFAULT_CAPACITY = 16;

    private transient volatile CounterCell[] counterCells;
    private transient volatile long baseCount;
    private transient volatile Node<K, V>[] table;
    private transient volatile int sizeCtl;

    private static final Unsafe U;
    private static final long SIZECTL;

    static {
        try {
            U = Unsafe.getUnsafe();
            Class<?> k = ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset
                    (k.getDeclaredField("sizeCtl"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    public int size() {
        long n = sumCount();
        return (n < 0L) ? 0 : (n > (long) Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) n;
    }

    @Override
    public boolean isEmpty() {
        return sumCount() <= 0L;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    /**
     * 查询时，首先计算出index，然后查看桶中元素，如果为null，则返回null，如果当前桶中元素的头节点的数据就是我们要查找的key，那么直接返回。
     * 如果当前节点的hashCode小于0 (因为红黑树的头节点的hashCode = -2)，那么采用红黑树find方法查找元素，否则采用链表的方式查找元素。
     * 由于哈希桶采用volatile修饰，所以table数组不存在线程可见性问题。这样就没必要加锁实现并发。
     */
    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public LixSet<K> ketSet() {
        return null;
    }

    @Override
    public LixCollection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    /**
     * 缓存系统是以缓存行为单位存储的，缓存行一般是2的整数幂个连续字节，一般为32-256字节，多线程修改互相独立的变量时，
     * 如果这些变量共享一个缓存行，就会影响彼此的性能，这就是伪共享。
     * 这个注解可以避免伪共享。
     */
    @Contended
    private static final class CounterCell {
        volatile long value;

        public CounterCell(long value) {
            this.value = value;
        }
    }

    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0) {
                Thread.yield(); // lost initialization race; just spin
            } else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K, V>[] nt =
                                (Node<K, V>[]) new Node<?, ?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

    private final int spread(int hash) {
        return (hash ^ (hash >>> 16)) & HASH_BITS;
    }

    private final long sumCount() {
        CounterCell a;
        CounterCell[] as = counterCells;
        long sum = baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; i++) {
                if ((a = as[i]) != null) {
                    sum += a.value;
                }
            }
        }
        return sum;
    }

    private static class Node<K, V> implements LixMap.Entry<K, V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;

        private Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return val;
        }

        public final int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        @Override
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }
}
