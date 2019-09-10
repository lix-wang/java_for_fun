package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;

/**
 * From {@link java.util.HashSet}
 *
 * HashSet本质是HashMap，只使用了HashMap的key，value统一设置为同一个Object对象。
 * TreeSet 是一个有序的二叉树。
 *
 * @author lix wang
 */
public class LixHashSet<E> extends LixAbstractCollection<E> {
    private transient LixHashMap<E, Object> map;

    public LixHashSet() {
        this.map = new LixHashMap<>();
    }

    @Override
    public @NotNull LixIterator<E> getIterator() {
        return null;
    }

    @Override
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(Object o) {
        return map.containsKey(o);
    }
}
