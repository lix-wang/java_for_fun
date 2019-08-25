package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;
import java.util.Comparator;

/**
 * From {@link java.util.List}
 */
public interface LixList<E> extends LixCollection<E> {
    /**
     * 添加目标元素到当前列表中
     *
     * @param element
     * @return
     */
    @Override
    boolean add(E element);

    /**
     * 根据索引返回一个元素。
     * 如果索引值超界，抛出{@link IndexOutOfBoundsException}
     *
     * @param index 索引值
     * @return
     */
    E get(int index);

    /**
     * 用新元素替代给定位置的元素，返回原来的元素。
     * 如果列表不支持该方法，抛出{@link UnsupportedOperationException}
     *
     * @param index
     * @param element
     * @return 原来的元素。
     */
    E set(int index, E element);

    /**
     * 删除特定位置的元素。
     * 如果列表不支持该方法，抛出{@link UnsupportedOperationException},
     * 如果索引越界，抛出{@link IndexOutOfBoundsException}
     *
     * @param index
     * @return 返回被移除的元素。
     */
    E remove(int index);

    /**
     * 删除首次出现该元素位置的元素。
     *
     * @param element 这里采用泛型类型限定，避免做类型判断。原始方法使用Object。
     * @return
     */
    @Override
    boolean remove(E element);

    /**
     * 添加目标集合中的所有元素到当前列表中。
     *
     * @param c 包含要添加元素的集合
     * @return
     */
    @Override
    boolean addAll(@NotNull LixCollection<? extends E> c);

    /**
     * 返回当前列表的列表迭代器。
     *
     * @return
     */
    LixListIterator<E> listIterator();

    /**
     * 根据索引范围，返回当前列表的子列表。
     * 如果范围越界，抛出{@link IndexOutOfBoundsException}
     *
     * @param fromIndex
     * @param toIndex
     * @return 子列表 fromIndex <= subList < toIndex
     */
    LixList<E> subList(int fromIndex, int toIndex);

    /**
     * 返回首次出现目标对象的位置。如果不存在返回-1。
     *
     * @param o
     * @return
     */
    int indexOf(Object o);

    /**
     * 返回最后出现目标对象的位置，如果不存在返回-1。
     *
     * @param o
     * @return
     */
    int lastIndexOf(Object o);

    /**
     * 根据传入的lambda，进行排序。
     * 这里没有使用default，让各具体列表去实现。
     * 如果不支持该方法，抛出{@link UnsupportedOperationException}
     *
     * @param c
     */
    void sort(Comparator<? super E> c);
}