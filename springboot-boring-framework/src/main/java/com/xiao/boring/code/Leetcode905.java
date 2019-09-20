package com.xiao.boring.code;

/**
 * 本题可以采用类似快排的方式。
 * 左右两端找满足条件的元素，互相交换位置。
 *
 * 结果：Runtime: 1 ms, faster than 100.00% of Java online submissions for Sort Array By Parity.
 * Memory Usage: 40.7 MB, less than 88.20% of Java online submissions for Sort Array By Parity.
 *
 * @author lix wang
 */
public class Leetcode905 {
    public int[] sortArrayByParity(int[] A) {
        sortArray(A, 0, A.length - 1);
        return A;
    }

    private void sortArray(int[] array, int startPos, int endPos) {
        int l, r;
        for (l = startPos; l <= endPos; l++) {
            if (array[l] % 2 != 0) {
                break;
            }
        }
        for (r = endPos; r >= startPos; r--) {
            if (array[r] % 2 == 0) {
                break;
            }
        }
        if (l >= r) {
            return;
        }
        int temp = array[l];
        array[l] = array[r];
        array[r] = temp;
        sortArray(array, l, r);
    }

    public static void main(String[] args) {
        Leetcode905 leetcode905 = new Leetcode905();
        int[] result = leetcode905.sortArrayByParity(new int[] {3, 1, 2, 4});
        for (int element : result) {
            System.out.println(element + " ");
        }
    }
}