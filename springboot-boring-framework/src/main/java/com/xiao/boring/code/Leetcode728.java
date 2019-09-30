package com.xiao.boring.code;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据范围求自除数的集合。
 *
 * Runtime: 3 ms, faster than 38.82% of Java online submissions for Self Dividing Numbers.
 * Memory Usage: 35.7 MB, less than 6.67% of Java online submissions for Self Dividing Numbers.
 *
 * @author lix wang
 */
public class Leetcode728 {
    public List<Integer> selfDividingNumbers(int left, int right) {
        List<Integer> list = new ArrayList<>();
        for (; left <= right; left++) {
            if (checkSelfDividingNumber(left)) {
                list.add(left);
            }
        }
        return list;
    }

    private boolean checkSelfDividingNumber(int number) {
        char[] chars = (number + "").toCharArray();
        if (chars.length <= 1) {
            return true;
        }
        for (char ch : chars) {
            int i = ch - '0';
            if (i == 0) {
                return false;
            }
            if (number % i != 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Leetcode728 leetcode728 = new Leetcode728();
        List<Integer> result = leetcode728.selfDividingNumbers(1, 22);
        for (int item : result) {
            System.out.println(item);
        }
    }
}
