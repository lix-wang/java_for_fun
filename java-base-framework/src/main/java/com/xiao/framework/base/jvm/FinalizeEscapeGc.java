package com.xiao.framework.base.jvm;

/**
 * 对象回收过程，不建议使用finalize方法。
 *
 * @author lix wang
 */
public class FinalizeEscapeGc {
    private static FinalizeEscapeGc SAVE_HOOK = null;

    public void isAlive() {
        System.out.println("I am still alive.");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Finalize method executed.");
        SAVE_HOOK = this;
    }

    public static void tryRelease() throws InterruptedException {
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500);
        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("I am dead.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new FinalizeEscapeGc();
        // 第一次尝试gc，对象会通过finalize自救
        tryRelease();
        // 第二次尝试gc，对象已经执行过finalize方法，无法再次自救
        tryRelease();
    }
}
