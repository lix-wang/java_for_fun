package com.xiao.framework.demo;

import com.xiao.framework.demo.ThreadNotSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A not thread safe example.
 *
 * @author lix wang
 */
@ThreadNotSafe
public class ListHelper<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<>());

    /**
     * Not thread safe, because lock of this method is ListHelper, but lock of list is another one.
     * So, it is a fake synchronized method.
     *
     * @param element
     * @return
     */
    public synchronized boolean putIfAbsent(E element) {
        boolean absent = !list.contains(element);
        if (absent) {
            list.add(element);
        }
        return absent;
    }

    /**
     * This method is thread safe, because the method lock same as the list.
     *
     * @param element
     * @return
     */
    public boolean addIfAbsent(E element) {
        synchronized (list) {
            boolean absent = !list.contains(element);
            if (absent) {
                list.add(element);
            }
            return absent;
        }
    }
}
