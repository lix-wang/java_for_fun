package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;
import java.util.Iterator;

/**
 * From {@link java.util.Iterator}
 * <p>
 * 迭代器接口，标识实现该接口的类具有迭代器功能。
 *
 * @param <E> E表示迭代器中元素的类型
 */
public interface LixIterator<E> extends Iterator<E> {
    /**
     * 判断是否还有元素来继续进行迭代操作。
     *
     * @return true 如果还有元素可以迭代，否则返回false。
     */
    @Override
    boolean hasNext();

    /**
     * 如果还有元素可以继续迭代，那么返回下一个元素，否则抛出异常。
     *
     * @return E 如果还有可迭代元素，否则抛出{@link UnsupportedOperationException}异常
     */
    @NotNull
    @Override
    E next();

    /**
     * 移除迭代器最后迭代的那个元素。
     */
    @Override
    default void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
