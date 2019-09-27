package com.xiao.framework.base.collection;

import java.util.Set;

/**
 * From {@link java.util.HashMap}
 *
 * HashMap中有两个重要的参数，容量、装载因子，容量是桶的数量，装载因子是桶填充的比例。
 * 当 actualSize > capacity * loadFactor 时，需要调整buckets的数量为当前的2倍。
 *
 * hash(key) 的算法为key.hashcode 高16位，和低16位做了异或处理。
 * 计算下标时，采用(n - 1) & hash 假设原始大小为16，那么相当于 15 & hash = 00..0001111 & hash
 * 扩容两倍，变成 31 & hash = 00..011111 & hash，这是两者之间区别在于尾部第5位，如果hash这一位为 0，那么&运算后，index与原来相等。
 * 如果hash这一位是1，那么index只有第5位变为1，相当于 + 16，也就是原来位置的2倍。所以一次扩容后，要么保持原位置，要么移动 capacity / 2。
 * 所以重新计算hash值时，只需要看hash新增的bit是0还是1，如果是0，增索引不变，如果是1，则变成 oldIndex + oldCapacity。
 * @author lix wang
 */
public class LixHashMap<K, V> implements LixMap<K, V> {
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private transient Node<K, V>[] table;
    private transient LixSet<LixMap.Entry<K, V>> entrySet;
    private transient int size = 0;
    private transient int modcount = 0;
    private transient int threshold;
    private transient float loadFactor;

    public LixHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node;
        return (node = getNode(hash(key), key)) == null ? null : node.getValue();
    }

    /**
     * 向散列中设置key、value，如果散列某个桶中冲突 >= 8，那么使用树结构来替代链表。
     * 因为，在长度为8时，8 / 2 = 4次，红黑树平均查找长度为log(n) 长度为8，平均查找长度为3次，
     * 如果长度 <= 6，树结构转换为链表，6 / 2 = 3，此时，由于链表转化为树和生成树的时间并不短，所以，没必要转换为树。
     * 之所以选择6、8，可以使用7来做缓冲，以免hashMap不停的插入、删除，导致频繁的发生树和链表的转换。
     */
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
     * 根据jdk，首先算出hash值，然后找到对应的index对应的entry，如果entry.key == key 或者 entry.key.equals(key) 那么返回这个节点，
     * 否则，如果这个entry.next instanceof TreeNode 那么通过搜索二叉树找到对应的节点。
     */
    Node<K, V> getNode(int hash, Object key) {
        return null;
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    static class Node<K, V> implements LixMap.Entry<K, V> {
        final int hash;
        final K key;
        V value;

        Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V val) {
            V oldVal = this.value;
            this.value = val;
            return oldVal;
        }
    }
}
