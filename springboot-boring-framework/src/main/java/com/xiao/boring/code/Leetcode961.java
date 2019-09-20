package com.xiao.boring.code;

/**
 * A数组2N个元素，N+1个不同元素，其中一个元素重复N次。那说明只有一个元素超过了两次。
 * 采用二分切分，递归查找。我们会发现不管怎么切分，切分后至少有一组连着四个包含这个元素，所以比较四次就行。
 *
 * 结果：Runtime: 0 ms, faster than 100.00% of Java online submissions for N-Repeated Element in Size 2N Array.
 * Memory Usage: 37.9 MB, less than 100.00% of Java online submissions for N-Repeated Element in Size 2N Array.
 *
 * @author lix wang
 */
public class Leetcode961 {
    public int repeatedNTimes(int[] A) {
        return repeatNTimes(A, 0, A.length - 1, new int[]{-1, -1, -1, -1});
    }

    private int repeatNTimes(int[] A, int startPos, int endPos, int[] elements) {
        int i;
        for (i = startPos; i < (startPos + 4) && i <= endPos; i++) {
            for (int element : elements) {
                if (element == A[i]) {
                    return element;
                }
            }
            elements[i - startPos] = A[i];
        }
        int midIndex = (endPos + startPos + 1) / 2;
        return repeatNTimes(A, midIndex, endPos, elements);
    }

    public static void main(String[] args) {
        Leetcode961 leetcode961 = new Leetcode961();
        int element = leetcode961.repeatedNTimes(new int[] {1, 2, 3, 3});
        System.out.println(element);
    }
}
