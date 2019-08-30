package com.xiao.framework.base.collection;

import com.xiao.framework.base.collection.util.RangeCheckUtils;

import javax.validation.constraints.NotNull;

import java.util.Comparator;

/**
 * From {@link java.util.Arrays}
 *
 * @author lix wang
 */
public class LixArrays {
    /**
     * 数组排序。
     * 对 0 <= range < size 某个区间进行排序。左闭右开。
     *
     * <p>
     *     原始方法中legacyMergeSort已经废弃。
     *
     *
     * @param targetArray
     * @param fromIndex
     * @param toIndex
     * @param comparator
     * @param <T>
     */
    public static <T> void sort(@NotNull T[] targetArray, int fromIndex, int toIndex,
            Comparator<? super T> comparator) {
        RangeCheckUtils.checkRange(fromIndex, toIndex, targetArray.length);
        if (comparator == null) {
            sort(targetArray, fromIndex, toIndex);
        } else {
            // TimSort.sort
            timSort(targetArray, fromIndex, toIndex, comparator);
        }
    }

    private static <T> void sort(@NotNull T[] targetArray, int fromIndex, int toIndex) {
        // 如果没有元素或者只有一个元素，那么根本不需要排序。
        int sortSize = toIndex - fromIndex;
        if (sortSize < 2) {
            return;
        }
        // 如果需要排序的元素数量小于23。
        if (sortSize < 32) {

        }
    }

    private static <T> void timSort(@NotNull T[] targetArray, int fromIndex, int toIndex, Comparator<T> comparator) {

    }

    /**
     * 找出从特定位置开始连续有序子数组，如果是倒序的，则需要进行翻转处理。
     *
     * @return 返回连续有序子数组的长度。
     */
    private static int countMaxAscSortedLength(@NotNull Object[] targetArray, final int fromIndex, final int toIndex) {
        int index = fromIndex + 1;
        // 如果该范围内，数量只有1，那么直接返回1。
        if (index == toIndex) {
            return 1;
        }
        // 如果开头两个元素是降序的。那么需要查找降序连续长度。
        if (((Comparable)targetArray[index]).compareTo(fromIndex) < 0) {

        } else {

        }
        return 1;
    }
}
