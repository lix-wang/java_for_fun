package com.xiao.framework.base.collection;

import java.util.Set;

/**
 * From {@link java.util.HashMap}
 *
 * @author lix wang
 */
public interface LixMap<K, V> {
    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    V get(Object key);

    V put(K key, V value);

    V remove(Object key);

    LixSet<K> ketSet();

    LixCollection<V> values();

    Set<LixMap.Entry<K, V>> entrySet();

    interface Entry<K, V> {
        K getKey();

        V getValue();

        V setValue(V value);
    }
}
