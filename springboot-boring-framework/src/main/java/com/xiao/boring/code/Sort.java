package com.xiao.boring.code;

import java.util.function.Function;

/**
 * @author lix wang
 */
public class Sort {
    /**
     * 快排，使用左右探测方式。
     */
    public static int[] quickSort(int[] source) {
        quickSort(source, 0, source.length - 1, null);
        return source;
    }

    public static int[] quickSort(int[] source, Function<Integer, Integer> comparator) {
        quickSort(source, 0, source.length - 1, comparator);
        return source;
    }

    private static void quickSort(int[] source, int startPos, int endPos, Function<Integer, Integer> comparator) {
        if (startPos > endPos) {
            return;
        }
        int i = startPos;
        int j = endPos;
        int temp = source[startPos];
        while (i < j) {
            while (getCompareValue(source[j], comparator) >= getCompareValue(temp, comparator) && j > i) {
                j--;
            }
            if (i < j) {
                source[i] = source[j];
                i++;
            }
            while (getCompareValue(source[i], comparator) <= getCompareValue(temp, comparator) && i < j) {
                i++;
            }
            if (i < j) {
                source[j] = source[i];
                j--;
            }
        }
        source[i] = temp;
        quickSort(source, startPos, i - 1, comparator);
        quickSort(source, j + 1, endPos, comparator);
    }

    private static int getCompareValue(Integer obj, Function<Integer, Integer> comparator) {
        if (comparator != null) {
            return comparator.apply(obj);
        } else {
            return obj;
        }
    }
}
