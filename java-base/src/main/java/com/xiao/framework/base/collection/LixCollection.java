package com.xiao.framework.base.collection;

import com.xiao.framework.base.collection.stream.LixStream;

import javax.validation.constraints.NotNull;

/**
 * From {@link java.util.Collection}
 * <p>
 * {@link java.util.List}、{@link java.util.Set}、{@link java.util.Queue}的父类接口。
 */
public interface LixCollection<E> extends LixIterable<E> {
    /**
     * 返回集合中的元素个数。
     *
     * @return int，如果个数大于{@link Integer#MAX_VALUE} 返回 {@link Integer#MAX_VALUE}
     */
    int size();

    /**
     * 检查集合是否为空。
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 判断集合中是否包含该元素。
     *
     * @param element 待检测的元素。这里使用Object，而不是使用E泛型做限定。
     *                在{@link #removeAll(LixCollection)}中对目标集合类型并没有限定，因此这里使用Object比较好。
     * @return true 如果包含，否则返回false。
     */
    boolean contains(Object element);

    /**
     * 如果添加元素成功返回true，否则返回false。
     * 如果该集合不允许执行该操作返回{@link UnsupportedOperationException}
     *
     * @param element
     * @return
     */
    boolean add(E element);

    /**
     * 从集合中移除该元素。
     * 如果移除成功返回true，否则返回false，如果集合不支持该操作抛出{@link UnsupportedOperationException}
     *
     * @param element 这里采用泛型类型限定，避免做类型判断。原始方法使用Object。
     * @return
     */
    boolean remove(E element);

    /**
     * 将c集合中的元素全部添加到当前集合中。
     * 如果当前集合不支持该操作，则抛出{@link UnsupportedOperationException}
     *
     * @param c 包含要添加元素的集合
     * @return true，如果全部添加成功。
     */
    boolean addAll(@NotNull LixCollection<? extends E> c);

    /**
     * 如果集合c中某元素在当前集合中也存在，那么删除当前集合中该元素。
     * 如果该集合不支持该方法，抛出{@link UnsupportedOperationException}
     *
     * @param c 目标集合没有做泛型类型限定。
     * @return true，如果所有交集元素都被删除，否则返回false。
     * @see #contains(Object)
     * @see #remove(Object)
     */
    boolean removeAll(@NotNull LixCollection<?> c);

    /**
     * 当前集合和目标集合c取交集。当前集合中只保留交集结果。
     * 如果当前集合不支持该方法，抛出{@link UnsupportedOperationException}
     *
     * @param c 目标集合没有泛型类型限定。
     * @return true 如果成功取交集。
     */
    boolean retainAll(@NotNull LixCollection<?> c);

    /**
     * 清除当前集合中的所有元素，如果当前集合不支持该方法，抛出{@link UnsupportedOperationException}
     */
    void clear();

    /**
     * 判断目标对象与当前集合是否相等。
     *
     * @param o
     * @return true，如果目标对象和当前集合相等
     */
    boolean equals(Object o);

    /**
     * 返回当前集合的哈希码。
     * 如果两个集合相等，那么这两个集合的哈希码一样。
     *
     * @return
     */
    int hashCode();

    /**
     * 返回包含当前集合的Object数组，这个数组是新创建的，
     * 因此改变该数组中的元素，并不会对当前集合造成影响。
     *
     * @return Object类型的数组，包含当前集合中的所有元素。
     * @see java.util.Arrays#asList(Object[])
     */
    Object[] toArray();

    /**
     * 根据特定的类类型，返回当前集合的特定数组。
     *
     * @param tClass
     * @param <T>
     * @return T[]，如果传入的类类型不是集合中每个元素类型的超类型，那么抛出{@link ArrayStoreException}
     */
    <T> T[] toArray(@NotNull Class<T> tClass);

    /**
     * 将集合转换成流。
     *
     * @return
     */
    default LixStream<E> stream() {
        throw new UnsupportedOperationException();
    }
}
