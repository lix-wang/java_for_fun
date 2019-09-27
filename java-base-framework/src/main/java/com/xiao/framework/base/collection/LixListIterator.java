package com.xiao.framework.base.collection;

public interface LixListIterator<E> extends LixIterator<E> {
    /**
     * 判断列表反向是否有元素可迭代。
     *
     * @return
     */
    boolean hasPrevious();

    /**
     * 返回列表反向迭代元素。
     */
    E previous();

    /**
     * 返回下一次迭代后的索引值。
     * 如果迭代到列表最后，返回列表的数量。
     *
     * @return
     */
    int nextIndex();

    /**
     * 返回向前一次迭代后的索引值，如果迭代到列表的头部，那么返回-1。
     *
     * @return
     */
    int previousIndex();

    void set(E e);
}
