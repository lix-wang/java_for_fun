package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;

/**
 * From {@link java.util.Set}
 */
public interface LixSet<E> extends LixCollection<E> {
    /**
     * 如果当前Set中不包含目标对象，并且添加成功，那么返回true，
     * 如果当前Set中包含目标对象，那么返回false。
     *
     * @param element
     * @return
     */
    @Override
    boolean add(E element);

    /**
     * 从当前Set中删除特定的元素。
     *
     * @param element 这里采用泛型类型限定，避免做类型判断。原始方法使用Object。
     * @return
     */
    @Override
    boolean remove(E element);

    /**
     * 将目标集合中的元素全部添加到当前Set中。
     *
     * @param c 包含要添加元素的集合
     * @return
     * @see #add(Object)
     */
    @Override
    boolean addAll(@NotNull LixCollection<? extends E> c);
}
