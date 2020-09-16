package com.xiao.framework.concurrency;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;
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
    private final ReentrantLock lock = new ReentrantLock();

    public LinkedTaskQueue() {
        this.capacity = Integer.MAX_VALUE;
    }

    @Override
    public boolean add(@NotNull E e) {
        if (count.get() >= capacity) {
            return false;
        }
        lock.lock();
        try {
            if (head == null) {
                head = end = new Node<>(e);
            } else {
                end = end.next = new Node<>(e);
            }
            count.getAndIncrement();
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public E take() {
        lock.lock();
        try {
            return takeFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(@NotNull Object o) {
        if (isEmpty()) {
            return false;
        }
        lock.lock();
        try {
            Node<E> prev = head;
            Node<E> node = prev.next;
            if (o.equals(prev.item)) {
                head = node;
                if (node == null) {
                    end = null;
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
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return count.get() <= 0;
    }

    @Override
    public E poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        long leftNanos =  timeUnit.toNanos(timeout);
        final long deadline = System.nanoTime() + leftNanos;
        E item;
        do {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            lock.lock();
            try {
                item = takeFirst();
            } finally {
                lock.unlock();
            }
            if (item != null) {
                break;
            }
            leftNanos = deadline - System.nanoTime();
        } while (leftNanos > 0);
        return item;
    }

    private E takeFirst() {
        if (isEmpty()) {
            return null;
        }
        E task;
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
    }

    private static class Node<E> {
        E item;
        Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
