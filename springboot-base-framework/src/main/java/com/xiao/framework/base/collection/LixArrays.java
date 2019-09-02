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
    private static final int MIN_MERGE = 32;

    /**
     * 数组排序。
     * 对 0 <= range < size 某个区间进行排序。
     * toIndex 最大的值为数组的size。
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

    /**
     * 没有Comparator的正序排序。
     * 本质上采用了归并排序 + 二叉插入排序。区别于归并排序的地方在于，归并排序是先完全细分到元素，也就是一个块一个元素，
     * 这里采用块，先划分块，进行块中排序，然后在把排序好的块，进行合并。
     *
     * @param targetArray
     * @param fromIndex
     * @param toIndex
     */
    private static void sort(@NotNull Object[] targetArray, int fromIndex, int toIndex) {
        // 如果没有元素或者只有一个元素，那么根本不需要排序。
        int sortSize = toIndex - fromIndex;
        if (sortSize < 2) {
            return;
        }
        // 如果需要排序的元素数量小于23。
        if (sortSize < MIN_MERGE) {
            // 查找出从起始位置开始连续有序子数组的长度。如果该数组是倒序的，那么需要翻转子数组。
            int orderLength = countMaxAscSortedLength(targetArray, fromIndex, toIndex);
            // 二叉插入排序
            binaryInsrtSort(targetArray, fromIndex, toIndex, fromIndex + orderLength);
            return;
        }
        // 否则使用插入排序加归并排序。
        int blockSize = minPerBlockLength(sortSize);
        // 计算有序子数组会有多少组
        int sortedSubSize = sortSize / blockSize + sortSize % blockSize == 0 ? 0 : 1;
        // 用来存储归并排序每个分区的信息。
        MergeSortStack mergeSortStack = new MergeSortStack(targetArray, sortedSubSize);
        while (sortSize > 0) {
            // 找出连续有序子集长度。
            int orderLength = countMaxAscSortedLength(targetArray, fromIndex, toIndex);
            // 如果有序长度小于块大小，这时才需要排序，否则已经有序就不用排序。
            int nextSortLength = 0;
            if (orderLength < blockSize) {
                // 算出下一次进行排序的元素个数。
                nextSortLength = Math.min(blockSize, sortSize);
                binaryInsrtSort(targetArray, fromIndex, fromIndex + nextSortLength, fromIndex + orderLength);
            }
            mergeSortStack.pushRange(fromIndex, fromIndex + nextSortLength);
            fromIndex += nextSortLength;
            sortSize -= nextSortLength;
        }
        mergeSortStack.mergeAllSortedStacks();
    }

    /**
     * 带有比较器的排序。
     *
     * @param targetArray
     * @param fromIndex
     * @param toIndex
     * @param comparator
     * @param <T>
     */
    private static <T> void timSort(@NotNull Object[] targetArray, int fromIndex, int toIndex,
            Comparator<T> comparator) {

    }

    /**
     * 获取归并排序每个块最小的数量。
     *
     * @param size
     * @return
     */
    private static int minPerBlockLength(int size) {
        int perBlockSize = 0;
        int odd = 0;
        while (size >= MIN_MERGE) {
            // 任何奇数 & 1 = 1，任何偶数 & 1 = 0，odd |= .. 相当于 odd = odd | ..
            odd |= (size & 1);
            // 数量除以 2 取整
            size >>= 1;
        }
        return size + odd;
    }

    /**
     * 找出从特定位置开始连续有序子数组，如果是倒序的，则需要进行翻转处理。
     * 0 <= fromIndex < toIndex <= size 找出从[fromIndex, toIndex) 连续最长的有序子数组。
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
        if (((Comparable) targetArray[index++]).compareTo(fromIndex) < 0) {
            while (index < toIndex && ((Comparable) targetArray[index]).compareTo(targetArray[index - 1]) < 0) {
                index++;
            }
            reverseRange(targetArray, fromIndex, index - 1);
        } else {
            while (index < toIndex && ((Comparable) targetArray[index]).compareTo(targetArray[index - 1]) >= 0) {
                index++;
            }
        }
        return index - fromIndex;
    }

    /**
     * 二分插入排序。
     * 根据参数，我们认为startIndex 之前的都是正序排列的。从startIndex开始进行二分插入排序。
     * 处理区间为[fromIndex, toIndex)
     *
     * @param targetArray
     * @param fromIndex
     * @param toIndex
     * @param startIndex
     */
    private static void binaryInsrtSort(@NotNull Object[] targetArray, final int fromIndex, final int toIndex,
            int startIndex) {
        RangeCheckUtils.checkRange(fromIndex <= startIndex && startIndex <= toIndex,
                String.format("Invalid params, fromIndex: %d startIndex: %d toIndex: %d",
                        fromIndex, startIndex, toIndex));
        // 二分插入排序。
        if (startIndex == fromIndex) {
            startIndex++;
        }
        for (; startIndex < toIndex; startIndex++) {
            Comparable originElement = (Comparable) targetArray[startIndex];
            int left = fromIndex;
            int right = toIndex;
            while (left < right) {
                int mid = (left + right) / 2;
                // mid > startIndex
                if (((Comparable) targetArray[mid]).compareTo(originElement) > 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            int moveNum = startIndex - left;
            if (moveNum == 1) {
                targetArray[left + 1] = targetArray[left];
            } else {
                System.arraycopy(targetArray, left, targetArray, left + 1, moveNum);
            }
            targetArray[left] = originElement;
        }
    }

    /**
     * 把 fromIndex <= ... <= toIndex 之间的元素，进行翻转。
     *
     * @param targetArray
     * @param fromIndex
     * @param toIndex
     */
    private static void reverseRange(@NotNull Object[] targetArray, final int fromIndex, final int toIndex) {
        for (int start = fromIndex, end = toIndex; start < end; start++, end--) {
            Object temp = targetArray[start];
            targetArray[start] = targetArray[end];
            targetArray[end] = temp;
        }
    }

    private static class MergeSortStack {
        private int index = 0;
        private int totalSize = 0;
        private Object[] targetArray;
        private final RangeNode[] rangeNodes;

        public MergeSortStack(Object[] targetArray, int size) {
            this.targetArray = targetArray;
            this.rangeNodes = new RangeNode[size];
        }

        public boolean pushRange(int fromIndex, int toIndex) {
            if (index < rangeNodes.length) {
                rangeNodes[index++] = new RangeNode(fromIndex, toIndex);
                totalSize += toIndex - fromIndex;
                return true;
            }
            return false;
        }

        /**
         * 把所有有序的分块合并。
         */
        public void mergeAllSortedStacks() {
           // todo 合并有序块。
        }

        private class RangeNode {
            private final int fromIndex;
            private final int toIndex;

            public RangeNode(int fromIndex, int toIndex) {
                this.fromIndex = fromIndex;
                this.toIndex = toIndex;
            }

            public int getFromIndex() {
                return fromIndex;
            }

            public int getToIndex() {
                return toIndex;
            }
        }
    }
}
