package com.xiao.framework.base.collection;

import com.xiao.framework.base.collection.util.RangeCheckUtils;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * From {@link java.util.AbstractList}
 *
 * @author lix wang
 */
public abstract class LixAbstractList<E> extends LixAbstractCollection<E> implements LixList<E> {
    /**
     * 在列表尾部添加一个元素。
     *
     * @param e
     * @return
     */
    @Override
    public boolean add(E e) {
        set(size(), e);
        return true;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /**
     * 如果范围越界，抛出{@link IndexOutOfBoundsException}
     *
     * @param index 索引值
     * @return
     */
    @Override
    abstract public E get(int index);

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(E element) {
        throw new UnsupportedOperationException();
    }

    /**
     * 返回首次出现该元素的位置，如果没有找到则返回-1。
     *
     * @param o
     * @return
     */
    @Override
    public int indexOf(Object o) {
        LixListIterator<E> iterator = listIterator();
        while (iterator.hasNext()) {
            if (Objects.equals(o, iterator.next())) {
                return iterator.previousIndex();
            }
        }
        return -1;
    }

    /**
     * 返回最后出现该元素的位置，如果没找到返回-1。
     *
     * @param o
     * @return
     */
    @Override
    public int lastIndexOf(Object o) {
        LixListIterator<E> iterator = listIterator(size());
        while (iterator.hasPrevious()) {
            if (Objects.equals(o, iterator.previous())) {
                return iterator.nextIndex();
            }
        }
        return -1;
    }

    @Override
    public void clear() {
        removeRange(0, size());
    }

    @Override
    public boolean addAll(int index, LixCollection<? extends E> c) {
        RangeCheckUtils.rangeCheckForAdd(index, this.size());
        boolean modified = false;
        for (E e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }

    /**
     * 本质上subList使用的还是原始的list，只是利用fromIndex和endIndex做了范围限制。
     *
     * @param fromIndex
     * @param toIndex
     * @return
     */
    @Override
    public LixList<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LixListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public LixListIterator<E> listIterator(final int index) {
        RangeCheckUtils.rangeCheckForAdd(index, this.size());
        return new LixListItr(index);
    }

    /**
     * 必须要两个列表的数量顺序元素都相同，那么才相等。
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LixList)) {
            return false;
        }
        LixListIterator<E> originIterator = listIterator();
        LixListIterator<E> targetIterator = ((LixList) obj).listIterator();
        while (originIterator.hasNext() && targetIterator.hasNext()) {
            E originObj = originIterator.next();
            Object targetObj = targetIterator.next();
            if (!Objects.equals(originObj, targetObj)) {
                return false;
            }
        }
        return !(originIterator.hasNext() || targetIterator.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this) {
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        LixListIterator<E> it = listIterator(fromIndex);
        for (int i = 0, n = toIndex - fromIndex; i < n; i++) {
            it.next();
            it.remove();
        }
    }

    /**
     * 表示当前列表被修改的次数。
     * 这里transient关键字，表示序列化时，忽略该字段。
     */
    protected transient int modCount = 0;

    /**
     * 迭代器基类，包含游标等
     */
    private class LixItr implements LixIterator<E> {
        /**
         * 游标值，表示执行{@link #next()}后，对应游标的索引值。
         * 例如在迭代器头，执行了一次{@link #next()}，这时，返回了0位置的元素，游标指向了1。
         */
        int cousor = 0;

        /**
         * 可以理解为上一个被迭代的元素的索引值。如果上一个被迭代的元素被删除，那么值为-1。
         * 例如在迭代器头，执行了一次{@link #next()}，那么被迭代的元素位置为0，lastRet值就是0。
         */
        int lastRet = -1;

        /**
         * 初始化时，将期望修改次数，设置为本列表已经修改了的次数。
         */
        int expectedModCount = modCount;

        /**
         * 如果当前游标并不是指向列表的末尾，那么表示，还可以继续执行{@link #next()}
         *
         * @return
         */
        @Override
        public boolean hasNext() {
            return cousor != size();
        }

        /**
         * 如果被修改了，那么抛出{@link ConcurrentModificationException}
         *
         * @return
         */
        @Override
        public E next() {
            checkModified();
            try {
                E element = get(cousor);
                lastRet = cousor;
                cousor++;
                return element;
            } catch (IndexOutOfBoundsException e) {
                checkModified();
                throw new NoSuchElementException();
            }
        }

        /**
         * 如果lastRet < 0，说明在执行该方法前并没有执行迭代方法，因此抛出{@link IllegalStateException}
         */
        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            checkModified();
            try {
                // 删除刚被迭代的元素。
                LixAbstractList.this.remove(lastRet);
                // 如果被迭代的元素位置小于游标位置，说明是使用next进行的迭代，那么删除后，游标需要-1，lastRet置-1.
                if (lastRet < cousor) {
                    cousor--;
                }
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * 如果期望的迭代器修改次数，与实际列表修改次数不同，那么抛出异常。
         */
        final void checkModified() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class LixListItr extends LixItr implements LixListIterator<E> {
        LixListItr(int index) {
            cousor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cousor != 0;
        }

        /**
         * 向前迭代。
         *
         * @return
         */
        @Override
        public E previous() {
            checkModified();
            try {
                int temp = cousor - 1;
                E previous = get(temp);
                lastRet = cousor = temp;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkModified();
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return cousor;
        }

        @Override
        public int previousIndex() {
            return cousor - 1;
        }

        @Override
        public void set(E e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            try {
                checkModified();
                LixAbstractList.this.set(lastRet, e);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
