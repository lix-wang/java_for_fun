package com.xiao.framework.base.collection;

import com.xiao.framework.base.collection.util.RangeCheckUtils;

import javax.validation.constraints.NotNull;

import java.util.Comparator;

/**
 * From {@link java.util.LinkedList}
 *
 * LinkedList是双向链表。
 *
 * @author lix wang
 */
public class LixLinkedList<E> extends LixAbstractList<E> {
    private transient int size = 0;
    private transient Node<E> first;
    private transient Node<E> last;

    @Override
    public E get(int index) {
        return getNode(index).item;
    }

    @Override
    public E set(int index, E element) {
        RangeCheckUtils.checkRange(index, this.size);
        Node<E> node = getNode(index);
        E oldValue = node.item;
        node.item = element;
        return oldValue;
    }

    @Override
    public E remove(int index) {
        RangeCheckUtils.checkRange(index, this.size);
        return removeNode(getNode(index));
    }

    @Override
    public void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull LixIterator<E> getIterator() {
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    private Node<E> getNode(final int index) {
        RangeCheckUtils.checkRange(index, this.size);
        // 左半侧
        if (index < (this.size >> 1)) {
            Node<E> target = this.first;
            for (int i = 0; i < index; i++) {
                target = target.next;
            }
            return target;
        } else {
            Node<E> target = this.last;
            for (int i = size - 1; i > index; i--) {
                target = target.prev;
            }
            return target;
        }
    }

    private E removeNode(Node<E> node) {
        Node<E> prev = node.prev;
        Node<E> next = node.next;
        E element = node.item;
        // 如果删除的是头节点
        if (prev == null) {
            this.first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        // 如果删除的是尾节点
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.item = null;
        size--;
        modCount++;
        return element;
    }

    private static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        public Node(E item, Node<E> prev, Node<E> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }
}
