package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

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
        checkRange(index);
        return (E) this.elementData[index];
    }

    @Override
    public E set(int index, E element) {
        checkRange(index);
        E oldValue = (E) this.elementData[index];
        this.elementData[index] = element;
        return oldValue;
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(this.size + 1);
        this.elementData[this.size ++] = e;
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
        rangeCheckForAdd(index);
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
        checkRange(index);
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
        rangeCheckForAdd(index);
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
        return false;
    }

    @Override
    public void sort(Comparator<? super E> c) {

    }

    @Override
    public @NotNull LixIterator<E> getIterator() {
        return null;
    }

    @Override
    public int size() {
        return this.size;
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

    private void checkRange(final int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private void rangeCheckForAdd(final int index) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }
}
