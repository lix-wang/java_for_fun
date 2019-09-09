package com.xiao.framework.base.collection;

import java.util.Set;

/**
 * From {@link java.util.HashMap}
 *
 * @author lix wang
 */
public class LixHashMap<K, V> implements LixMap<K, V> {
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    transient private Node<K, V>[] table;
    transient private LixSet<LixMap.Entry<K, V>> entrySet;
    transient private int size = 0;
    transient private int modcount = 0;
    transient private int threshold;
    transient private float loadFactor;

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
        return false;
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
    private Node<K, V> getNode(int hash, Object key) {
        return null;
    }

    private static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private static class Node<K, V> implements LixMap.Entry<K, V> {
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
