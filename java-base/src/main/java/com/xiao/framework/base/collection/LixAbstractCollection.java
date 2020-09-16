package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * From {@link java.util.AbstractCollection}
 *
 * @author lix wang
 */
public abstract class LixAbstractCollection<E> implements LixCollection<E> {
    /**
     * 这里根据注解是说有些虚拟机数组中会有header words，所以数组最大容量要 -8。
     * 有资料说是因为要兼容32位虚拟机的原因，最大数量是 2^31 = 2147483648。数组本身要8bytes(long)来存储这个数量值，
     * 所以最大容量是2^31 - 8。
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    @NotNull
    @Override
    public abstract LixIterator<E> getIterator();

    @Override
    public abstract int size();

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        LixIterator<E> it = getIterator();
        if (o == null) {
            while (it.hasNext()) {
                if (it.next() == null) {
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 这是原本jdk中实现集合转Object[]的方法，注释说即使在转换过程中新增了元素，也能确保object[]中的数量与集合元素数量一致。
     * 个人感觉并不能保证？首先是根据集合大小创建一个数组，然后如果转换完集合中元素，发现在转换中又新增了元素，
     * 那么使用{@link #finishToArray(Object[], Iterator)} 来继续转换。在继续转换方法中采用循环来判断是否有新元素增加，直到没有，
     * 然后判断是否数组大小超过集合大小，如果超过则使用集合大小来缩小数组大小。但既然在{@link #toArray()} 方法中，
     * 转换过程中可能产生新增元素，那么在{@link #finishToArray(Object[], Iterator)} 结束while循环后，也一样可能新增了元素，
     * 因此这里严格来讲，并不能完全保证完整正确的进行集合到数组的转换。
     *
     */
    @Override
    public Object[] toArray() {
        return toArray(Object.class);
    }

    /**
     * 这里采用传入对象类型的方式，将集合转换为特定类型的数组。
     *
     * @param tClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T[] toArray(@NotNull Class<T> tClass) {
        // Estimate size of array; be prepared to see more or fewer elements
        T[] r = (T[]) Array.newInstance(tClass.getComponentType(), size());
        LixIterator<E> it = getIterator();
        for (int i = 0; i < r.length; i++) {
            // fewer elements than expected
            if (!it.hasNext()) {
                return Arrays.copyOf(r, i);
            }
            r[i] = (T) it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * 在这个抽象类中默认实现是抛异常。
     *
     * @param e
     * @return
     */
    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    /**
     * 移除元素。
     * 原本这里根据element是否为空，使用两个分支，这里采用{@link Objects#equals(Object, Object)} 来简化代码。
     *
     * @param element 这里采用泛型类型限定，避免做类型判断。原始方法使用Object。
     * @return
     */
    @Override
    public boolean remove(E element) {
        LixIterator<E> iterator = getIterator();
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next(), element)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * 添加所有元素。这里没有考虑元素重复的问题。
     *
     * 原始实现用modified标识符来判断是否全部添加成功，但感觉写的有问题，如果前面元素{@link #add(Object)}方法返回false，
     * 最后一次添加返回true，那么结果仍然是true。所以这里去除了标识符。
     *
     * @param c 包含要添加元素的集合
     * @return
     */
    @Override
    public boolean addAll(@NotNull LixCollection<? extends E> c) {
        c.forEach(element -> add(element));
        return true;
    }

    /**
     * 移除当前集合中所有同时存在于目标集合的元素。
     * 感觉这里原始实现中的标识符也没什么用，只能判断最后一个remove是否成功。
     *
     * @param c 目标集合没有做泛型类型限定。
     * @return
     */
    @Override
    public boolean removeAll(@NotNull LixCollection<?> c) {
        LixIterator<?> it = getIterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
            }
        }
        return true;
    }

    /**
     * 移除存在于当前集合但不存在于目标集合的元素。用来求交集。
     *
     * 这里原始实现中的标识符同样感觉没什么用。
     *
     * @param c 目标集合没有泛型类型限定。
     * @return
     */
    @Override
    public boolean retainAll(@NotNull LixCollection<?> c) {
        LixIterator<E> it = getIterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
            }
        }
        return true;
    }

    /**
     * 清空当前集合。
     */
    @Override
    public void clear() {
        LixIterator<E> it = getIterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0) {
                    newCap = hugeCapacity(cap + 1);
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T) it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
        {
            throw new OutOfMemoryError
                    ("Required array size too large");
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
