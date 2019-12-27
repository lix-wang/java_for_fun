package com.xiao.framework.demo;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用CAS来构建非阻塞栈
 *
 * @author lix wang
 */
@ThreadSafe
public class ConcurrentStack<E> {
    AtomicReference<Node<E>> top = new AtomicReference<>();

    public void push(E item) {
        Node<E> head = new Node<>(item);
        Node<E> oldHead;
        do {
            oldHead = top.get();
            head.next = oldHead;
        } while (!top.compareAndSet(oldHead, head));
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private static class Node<E> {
        private final E item;
        private Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
