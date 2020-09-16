package com.xiao.framework.demo;

import com.xiao.framework.base.exception.LixException;

/**
 * Money transfer between two accounts.
 *
 * @author lix wang
 */
public class TransferMoneyDemo {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    private static final Object tieLock = new Object();

    /**
     * 这种方式可能导致锁顺序死锁，例如 A transfer to B, B transfer to A 同时发生，那么可能会导致锁顺序死锁。
     */
    @ThreadNotSafe
    public void transferMoney(Account fromAccount, Account toAccount, float amount) throws InsufficientFundsException {
        synchronized (fromAccount){
            synchronized (toAccount) {
                if (fromAccount.getLeftMoney() < amount) {
                    throw new InsufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }

    /**
     * 采用这种方式，不管 A transfer to B 还是 B transfer to A，获取锁的顺序是一致的，并发线程获取锁的顺序一致，那么就不会导致锁顺序死锁。
     */
    @ThreadSafe
    public void transferMoney2(final Account fromAccount, final Account toAccount, final float amount)
            throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAccount.getLeftMoney() < amount) {
                    throw new InsufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }

        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        if (fromHash < toHash) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }

    private static class Account {
        private float leftMoney;

        public float getLeftMoney() {
            return leftMoney;
        }

        public void debit(float amount) {
            this.leftMoney -= amount;
        }

        private void credit(float amount) {
            this.leftMoney += amount;
        }
    }

    private static class InsufficientFundsException extends LixException {
        public InsufficientFundsException() {
        }
    }
}
