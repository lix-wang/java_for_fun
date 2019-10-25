package com.xiao.framework.demo;

import com.xiao.framework.pool.ThreadPoolHelper;

/**
 *
 * @author lix wang
 */
public class TaskExecutorTest {
    private static void test() {
        for (int i = 0; i < 100; i++) {
            print(i);
        }
    }

    private static void print(final int i) {
        ThreadPoolHelper.taskPool().submit(() -> System.out.println("Task " + i));
    }

    public static void main(String[] args) {
        test();
    }
}
