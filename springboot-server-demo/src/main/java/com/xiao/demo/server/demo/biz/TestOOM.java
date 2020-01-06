package com.xiao.demo.server.demo.biz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lix wang
 */
public class TestOOM {
    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
        }
    }
}
