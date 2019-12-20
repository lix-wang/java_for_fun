package com.xiao.framework.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author lix wang
 */
public class LockHolderDemo {
    private final Map<String, String> attributes = new HashMap<>();

    /**
     * 内部锁持有过长的时间，只有Map.get这个方法才真正的需要锁。
     */
    @ThreadSafe
    public synchronized boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get(key);
        if (location == null) {
            return false;
        } else {
            return Pattern.matches(regexp, location);
        }
    }

    /**
     * 只在真正需要加锁的操作中加锁，这样能减少锁持有的时间。
     */
    @ThreadSafe
    public boolean userLocationMatchesBetter(String name, String regexp) {
        String key = "users." + name + ".location";
        String location;
        synchronized (this) {
            location = attributes.get(key);
        }
        if (location == null) {
            return false;
        } else {
            return Pattern.matches(regexp, location);
        }
    }

    public synchronized void getHello() throws InterruptedException {
        System.out.println("Get hello lock.");
        Thread.sleep(3000);
        System.out.println("Hello");
    }

    public synchronized void getWorld() {
        System.out.println("World");
    }

    public static void main(String[] args) {
        LockHolderDemo lockHolderDemo = new LockHolderDemo();
        new Thread(() -> {
            try {
                lockHolderDemo.getHello();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            System.out.println("Start get world");
            lockHolderDemo.getWorld();
        }).start();
    }
}
