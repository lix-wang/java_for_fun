package com.xiao.framework.concurrency;

import javax.validation.constraints.NotNull;

/**
 *
 * @author lix wang
 */
public interface TaskQueue<E> {
    /**
     * Insert an element into taskQueue.
     *
     * @param e
     * @return
     */
    boolean add(@NotNull E e);

    /**
     * Retrieves and removes the head of taskQueue.
     *
     * @return
     */
    E take();

    /**
     * Remove first matched element from taskQueue.
     *
     * @param o
     * @return
     */
    boolean remove(@NotNull Object o);

    /**
     * whether taskQueue is empty.
     *
     * @return
     */
    boolean isEmpty();
}
