package com.xiao.framework.thread.demo;

import com.xiao.framework.thread.ThreadNotSafe;
import com.xiao.framework.thread.ThreadSafe;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Demo of safe thread.
 *
 * @author lix wang
 */
@ThreadSafe
public class SafeThreadDemo {
    private long lastNumber;
    private List<Long> lastFactors = new ArrayList<>();

    private final Set<Person> personSet = new HashSet<>();

    /**
     *  Intrinsic lock.
     *
     * @param param
     * @return
     */
    public synchronized long service(long param) {
        long result;
        if (param == lastNumber) {
            result = lastFactors.get(lastFactors.size() - 1);
        } else  {
            lastNumber = param;
            result = lastNumber * lastNumber;
            lastFactors.add(result);
        }
        return result;
    }

    public synchronized void addPerson(@NotNull Person person) {
        personSet.add(person);
    }

    public synchronized boolean containsPerson(@NotNull Person person) {
        return personSet.contains(person);
    }

    @ThreadNotSafe
    public static class Person {
        public int age;
        private String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }
}
