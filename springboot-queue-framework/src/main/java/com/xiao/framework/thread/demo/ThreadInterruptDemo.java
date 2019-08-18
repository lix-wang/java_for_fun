package com.xiao.framework.thread.demo;

public class ThreadInterruptDemo {
    private static Thread doInterrupt() {
        Runnable runnable = () -> {
            System.out.println("I started a thread.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("I was interrupted");
            }
            System.out.println("I finished a thread.");
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static void main(String[] args) {
        Thread thread = doInterrupt();
        // 像线程发送中断请求，线程中断状态被设置为true，如果目前线程被sleep调用，抛出interruptedException。
        thread.interrupt();
        // 检查线程是否被终止，不改变线程中断状态。
        System.out.println("Thread state: " + thread.isInterrupted());
        // 判断当前线程中断状态，会将当前线程中断状态重置为false
        System.out.println("Thread state: " + Thread.interrupted());
    }
}
