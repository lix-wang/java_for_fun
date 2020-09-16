package com.xiao.framework.demo;

import com.xiao.framework.util.ThreadPoolHelper;

/**
 *
 * @author lix wang
 */
public class TaskExecutorTest {
    private static void test() {
        for (int i = 0; i < 10000; i++) {
            print(i);
        }
    }

    private static void print(final int i) {
        ThreadPoolHelper.taskPool().submit("task " + i, () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Task " + i);
        });
    }

    public static void main(String[] args) {
        test();
    }
}
