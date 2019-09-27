package com.xiao.framework.base.collection;

/**
 * From {@link java.util.LinkedHashMap}
 *
 * LinkedHashMap 继承自HashMap，定义了LinkedHashMap.Entry类，重写了newNode()方法。维护一个运行于所有条目的双重链表列表，
 * 这个链表定义了迭代顺序，一般默认是插入顺序，这样就能按照插入的顺序迭代元素。
 *
 * @author lix wang
 */
public class LixLinkedHashMap<K, V> extends LixHashMap<K, V> {
    final boolean accrssOrder;

    public LixLinkedHashMap() {
        super();
        accrssOrder = false;
    }

    public V get(Object key) {
        Node<K, V> node;
        if ((node = getNode(hash(key), key)) == null) {
            return null;
        }
        return node.getValue();
    }
}
