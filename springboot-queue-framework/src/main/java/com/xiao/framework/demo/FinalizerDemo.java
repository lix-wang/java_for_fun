package com.xiao.framework.demo;

/**
 *
 * @author lix wang
 */
public class FinalizerDemo {
    public void print() {
        System.out.println("Hello world.");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("I am finalized.");
    }

    public static void main(String[] args) {
        FinalizerDemo finalizerDemo = new FinalizerDemo();
        finalizerDemo.print();
    }
}
