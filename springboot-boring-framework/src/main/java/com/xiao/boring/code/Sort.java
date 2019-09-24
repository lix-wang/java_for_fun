package com.xiao.boring.code;

/**
 * @author lix wang
 */
public class Sort {
    /**
     * 快排，使用左右探测方式。
     */
    public static int[] quickSort(int[] source) {
        quickSort(source, 0, source.length - 1);
        return source;
    }

    private static void quickSort(int[] source, int startPos, int endPos) {
        if (startPos > endPos) {
            return;
        }
        int i = startPos;
        int j = endPos;
        int temp = source[startPos];
        while (i < j) {
            while (source[j] >= temp && j > i) {
                j--;
            }
            if (i < j) {
                source[i] = source[j];
                i++;
            }
            while (source[i] <= temp && i < j) {
                i++;
            }
            if (i < j) {
                source[j] = source[i];
                j--;
            }
        }
        source[i] = temp;
        quickSort(source, startPos, i - 1);
        quickSort(source, j + 1, endPos);
    }
}
