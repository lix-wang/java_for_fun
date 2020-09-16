package com.xiao.framework.base.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lix wang
 */
public class InvokeDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Good Day");
        ArrayList<String> als = new ArrayList<>();
        als.add("Bad Day");
        print();
        InvokeRunnable invokeRunnable = () -> System.out.println("Invoke Runnable.");
        invokeRunnable.run();
    }

    private static void print() {
        System.out.println("Hello world!");
    }

    @FunctionalInterface
    private interface InvokeRunnable {
        void run();
    }
}
