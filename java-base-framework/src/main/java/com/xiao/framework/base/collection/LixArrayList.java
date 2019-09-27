package com.xiao.framework.base.collection;

import com.xiao.framework.base.collection.util.RangeCheckUtils;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * From {@link java.util.ArrayList}
 *
 * @author lix wang
 */
public class LixArrayList<E> extends LixAbstractList<E> implements Serializable {
    private static final long serialVersionUID = 4662462892162163089L;

    private static final Object[] EMPTY_DATA_ELEMENT = {};

    private transient Object[] elementData;
    private int size;

    public LixArrayList() {
        this.elementData = EMPTY_DATA_ELEMENT;
    }

    public LixArrayList(int initCapacity) {
        if (initCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initCapacity);
        } else if (initCapacity == 0) {
            this.elementData = EMPTY_DATA_ELEMENT;
        } else {
            this.elementData = new Object[initCapacity];
        }
    }

    public LixArrayList(@NotNull LixCollection<? extends E> c) {
        this.elementData = c.toArray();
        if ((this.size = this.elementData.length) > 0) {
            if (this.elementData.getClass() != Object[].class) {
                this.elementData = Arrays.copyOf(this.elementData, this.size, Object[].class);
            }
        } else {
            this.elementData = EMPTY_DATA_ELEMENT;
        }
    }

    @Override
    public E get(int index) {
        RangeCheckUtils.checkRange(index, this.size);
        return (E) this.elementData[index];
    }

    @Override
    public E set(int index, E element) {
        RangeCheckUtils.checkRange(index, this.size);
        E oldValue = (E) this.elementData[index];
        this.elementData[index] = element;
        return oldValue;
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(this.size + 1);
        this.elementData[this.size++] = e;
        return true;
    }

    /**
     * 相当于在特定索引位插入一个元素，原来index - size位置的元素，依次后移一位。
     *
     * @param index
     * @param element
     */
    @Override
    public void add(int index, E element) {
        RangeCheckUtils.rangeCheckForAdd(index, this.size);
        ensureCapacity(this.size + 1);
        System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
        elementData[index] = element;
        this.size++;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.elementData, this.size);
    }

    @Override
    public <T> T[] toArray(@NotNull Class<T> tClass) {
        T[] r = (T[]) Array.newInstance(tClass.getComponentType(), this.size);
        System.arraycopy(this.elementData, 0, r, 0, this.size);
        return r;
    }

    /**
     * 避免构造迭代器对象。
     *
     * @param o
     * @return
     */
    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < this.size; i++) {
            if (Objects.equals(elementData[i], o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = this.size - 1; i >= 0; i--) {
            if (Objects.equals(elementData[i], o)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 删除当前位置元素，并且把当前位置之后的元素依次向前移动，最后把数组尾部元素置空。
     *
     * @param index
     * @return
     */
    @Override
    public E remove(int index) {
        RangeCheckUtils.checkRange(index, this.size);
        modCount++;
        E oldValue = (E) elementData[index];
        // not remove end element of array
        int moveNum = this.size - index - 1;
        if (moveNum > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, moveNum);
        }
        this.elementData[--size] = null;
        return oldValue;
    }

    @Override
    public boolean remove(E element) {
        for (int i = 0; i < this.size; i++) {
            if (Objects.equals(this.elementData[i], element)) {
                fastRemove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        modCount++;
        for (int i = 0; i < this.size; i++) {
            this.elementData[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean addAll(@NotNull LixCollection<? extends E> c) {
        Object[] a = c.toArray();
        if (a.length > 0) {
            ensureCapacity(this.size + a.length);
            System.arraycopy(a, 0, this.elementData, size, a.length);
            this.size += a.length;
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, LixCollection<? extends E> c) {
        RangeCheckUtils.rangeCheckForAdd(index, this.size);
        Object[] a = c.toArray();
        ensureCapacity(this.size + a.length);
        int moveNum = this.size - index;
        if (moveNum > 0) {
            System.arraycopy(this.elementData, index, this.elementData, index + a.length, moveNum);
        }
        System.arraycopy(a, 0, this.elementData, index, a.length);
        this.size += a.length;
        return a.length > 0;
    }

    @Override
    public boolean removeAll(@NotNull LixCollection<?> c) {
        return batchRemove(c, false);
    }

    @Override
    public boolean retainAll(@NotNull LixCollection<?> c) {
        return batchRemove(c, true);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = this.modCount;
        LixArrays.sort((E[]) elementData, 0, size, c);
        if (expectedModCount != this.modCount) {
            throw new ConcurrentModificationException();
        }
        this.modCount++;
    }

    @Override
    public @NotNull LixIterator<E> getIterator() {
        return new Itr();
    }

    @Override
    public LixListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public LixListIterator<E> listIterator(int index) {
        RangeCheckUtils.rangeCheckForAdd(index, this.size);
        return new ListItr(index);
    }

    @Override
    public int size() {
        return this.size;
    }

    /**
     * 使用原始的数组遍历，并且使用modCount检验，判断在遍历过程中，是否有元素被修改。
     *
     * @param action 遍历元素执行相应的行为。
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = this.modCount;
        for (int i = 0; expectedModCount == modCount && i < this.size; i++) {
            action.accept((E) this.elementData[i]);
        }
        if (expectedModCount != this.modCount) {
            throw new ConcurrentModificationException();
        }
    }

    private boolean batchRemove(LixCollection<?> c, boolean condition) {
        final Object[] elementData = this.elementData;
        int index = 0;
        int matchIndex = 0;
        boolean modified = false;
        try {
            // 把匹配上的元素都依次移到数组头部。
            for (; index < this.size; index++) {
                if (c.contains(elementData[index]) == condition) {
                    elementData[matchIndex++] = elementData[index];
                }
            }
        } finally {
            // 处理过程有异常, 把未处理的元素依次移动到数组头部。
            if (index != this.size) {
                System.arraycopy(elementData, index, elementData, matchIndex, this.size - index);
                matchIndex += this.size - index;
            }
            // 如果不是所有的元素都匹配上，那么把未匹配上的元素置空。
            if (matchIndex != size) {
                for (int i = matchIndex; i < this.size; i++) {
                    elementData[i] = null;
                }
                modCount += this.size - matchIndex;
                this.size = matchIndex;
                modified = true;
            }
        }
        return modified;
    }

    private void fastRemove(final int index) {
        this.modCount++;
        int moveNum = this.size - index - 1;
        if (moveNum > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, moveNum);
        }
        this.elementData[--size] = null;
    }

    private void ensureCapacity(int minCapacity) {
        // todo
    }

    private class Itr implements LixIterator<E> {
        int cursor;
        int lastRet = -1;
        int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor != LixArrayList.this.size;
        }

        @Override
        public E next() {
            checkForComodification();
            int i = cursor;
            if (i >= size) {
                throw new NoSuchElementException();
            }
            Object[] elementData = LixArrayList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                LixArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class ListItr extends Itr implements LixListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0) {
                throw new NoSuchElementException();
            }
            Object[] elementData = LixArrayList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        public void set(E e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                LixArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                LixArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
