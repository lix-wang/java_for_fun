package com.xiao.framework.base.collection;

/**
 * From {@link java.util.Queue}
 */
public interface LixQueue<E> extends LixCollection<E> {
    /**
     * 将目标元素插入到队列中，如果可以立即执行，并且不会违反容量限制，那么在插入后返回true，
     * 如果当前没有可用空间，则抛出{@link IllegalStateException}。
     *
     * @param element
     * @return
     */
    @Override
    boolean add(E element);

    /**
     * 将目标元素插入到队列中，如果可以立即执行，并且不会违反容量限制，那么插入后返回true，
     * 如果没有可用空间返回false。与{@link #add(Object)} 区别在于不会抛异常而是返回false。
     *
     * @param e
     * @return
     */
    boolean offer(E e);

    /**
     * 检索并删除队列头。
     *
     * @return 返回队列头元素。
     * @throws java.util.NoSuchElementException 如果队列为空，抛出该异常。
     */
    E remove();

    /**
     * 检索并删除队列头。与{@link #remove()}的区别在于，如果队列为空，不会抛出异常，会返回空。
     *
     * @return 队列头元素。如果队列为空，返回null。
     */
    E poll();

    /**
     * 检索队列头，但并不会移除队列头。如果是空队列，抛出{@link java.util.NoSuchElementException}。
     *
     * @return 队列头元素。
     * @throws java.util.NoSuchElementException，如果队列为空。
     */
    E element();

    /**
     * 检索队列头，并不会移除队列头。与{@link #element()}的区别在于空队列时不会抛异常，会返回null。
     *
     * @return 队列头元素，如果队列不为空，否则返回null。
     */
    E peek();
}
