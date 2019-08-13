package com.xiao.framework.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo of safe thread.
 *
 * @author wang lingxiao(lix.wang@alo7.com)
 */
@ThreadSafe
public class SafeThreadDemo {
    private long lastNumber;
    private List<Long> lastFactors = new ArrayList<>();

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
}
