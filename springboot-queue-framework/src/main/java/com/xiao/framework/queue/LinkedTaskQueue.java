package com.xiao.framework.queue;

import javax.validation.constraints.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author lix wang
 */
public class LinkedTaskQueue<E> implements TaskQueue<E> {
    private final int capacity;
    private Node<E> head;
    private Node<E> end;
    private final AtomicInteger count = new AtomicInteger(0);
    private final ReentrantLock addLock = new ReentrantLock();
    private final ReentrantLock takeLock = new ReentrantLock();
    private final ReentrantLock removeLock = new ReentrantLock();

    public LinkedTaskQueue() {
        this.capacity = Integer.MAX_VALUE;
    }

    @Override
    public boolean add(@NotNull E e) {
        if (count.get() >= capacity) {
            return false;
        }
        addLock.lock();
        try {
            if (head == null) {
                head = end = new Node<>(e);
            } else {
                end = end.next = new Node<>(e);
            }
            count.getAndIncrement();
        } finally {
            addLock.unlock();
        }
        return true;
    }

    @Override
    public E take() {
        E task = null;
        if (isEmpty()) {
            return task;
        }
        takeLock.lock();
        try {
            // double check
            if (isEmpty()) {
                return task;
            }
            if (count.get() <= 1) {
                task = head.item;
                head.item = null;
                head = end = null;
            } else {
                task = head.item;
                Node<E> node = head.next;
                head.next = null;
                head.item = null;
                head = node;
            }
            count.getAndDecrement();
            return task;
        } finally {
            takeLock.unlock();
        }
    }

    @Override
    public boolean remove(@NotNull Object o) {
        removeLock.lock();
        try {
            if (isEmpty()) {
                return false;
            }
            Node<E> prev = head;
            Node<E> node = prev.next;
            if (o.equals(prev.item)) {
                head = node;
                if (node == null) {
                    end = node;
                }
                prev.item = null;
                return true;
            }
            while (prev != null && node != null) {
                if (o.equals(node.item)) {
                    prev.next = node.next;
                    node.item = null;
                    if (node.next != null) {
                        node.next = null;
                    } else {
                        end = prev;
                    }
                    return true;
                }
                prev = node;
                node = node.next;
            }
            return false;
        } finally {
            removeLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return count.get() <= 0;
    }

    private static class Node<E> {
        E item;
        Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
