package com.xiao.boring.code;

import java.util.function.Function;

/**
 * 先abs，再pow，然后再排序。
 *
 * Runtime: 8 ms, faster than 11.82% of Java online submissions for Squares of a Sorted Array.
 * Memory Usage: 41.5 MB, less than 95.73% of Java online submissions for Squares of a Sorted Array.
 *
 * @author lix wang
 */
public class Leetcode977 {
    public int[] sortedSquares(int[] A) {
        for (int i = 0; i < A.length; i++) {
            A[i] = pow(abs(A[i]));
        }
        quickSort(A, 0, A.length - 1, null);
        return A;
    }

    private void quickSort(int[] source, int startPos, int endPos, Function<Integer, Integer> comparator) {
        if (startPos > endPos) {
            return;
        }
        int temp = source[startPos];
        int i = startPos;
        int j = endPos;
        while (i < j) {
            while (getCompareValue(source[j], comparator) >= getCompareValue(temp, comparator) && j > i) {
                j--;
            }
            if (j > i) {
                source[i] = source[j];
                i ++;
            }
            while (getCompareValue(source[i], comparator) <= getCompareValue(temp, comparator) && i < j) {
                i ++;
            }
            if (j > i) {
                source[j] = source[i];
                j--;
            }
        }
        source[i] = temp;
        quickSort(source, startPos, i - 1, comparator);
        quickSort(source, j + 1, endPos, comparator);
    }

    private int getCompareValue(int item, Function<Integer, Integer> comparator) {
        if (comparator == null) {
            return item;
        } else {
            return comparator.apply(item);
        }
    }

    private int abs(int x) {
        if (x < 0) {
            return - x;
        }
        return x;
    }

    private int pow(int x) {
        return x * x;
    }

    public static void main(String[] args) {
        Leetcode977 leetcode977 = new Leetcode977();
        int[] result = leetcode977.sortedSquares(new int[]{-4, -1, 0, 3, 10});
        for (int item : result) {
            System.out.println(item);
        }
    }
}
