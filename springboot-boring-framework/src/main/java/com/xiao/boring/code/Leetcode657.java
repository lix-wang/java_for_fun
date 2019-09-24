package com.xiao.boring.code;

/**
 * 这道题很简单，只要判断左右前后走的数量是否相等就行。(为什么Runtime很差？？？)
 *
 * 结果：Runtime: 9 ms, faster than 37.96% of Java online submissions for Robot Return to Origin.
 * Memory Usage: 36.8 MB, less than 100.00% of Java online submissions for Robot Return to Origin.
 *
 * 采用judgeCircle2方法，由于toCharArray() 产生new char[] 对象，因此占用内存更多。但是要更快一点。
 * 结果：Runtime: 7 ms, faster than 64.12% of Java online submissions for Robot Return to Origin.
 * Memory Usage: 37.7 MB, less than 100.00% of Java online submissions for Robot Return to Origin.
 *
 * @author lix wang
 */
public class Leetcode657 {
    public boolean judgeCircle(String moves) {
        int horizCount = 0; // 横
        int vertexCount = 0; // 纵
        for (int i = 0; i < moves.length(); i++) {
            switch (moves.charAt(i)) {
                case 'U':
                    vertexCount++;
                    break;
                case 'D':
                    vertexCount--;
                    break;
                case 'L':
                    horizCount++;
                    break;
                case 'R':
                    horizCount--;
                    break;
            }
        }
        if (horizCount != 0 || vertexCount != 0) {
            return false;
        }
        return true;
    }

    public boolean judgeCircle2(String moves) {
        int horizCount = 0; // 横
        int vertexCount = 0; // 纵
        for (char ch : moves.toCharArray()) {
            switch (ch) {
                case 'U':
                    vertexCount++;
                    break;
                case 'D':
                    vertexCount--;
                    break;
                case 'L':
                    horizCount++;
                    break;
                case 'R':
                    horizCount--;
                    break;
            }
        }
        if (horizCount != 0 || vertexCount != 0) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Leetcode657 leetcode657 = new Leetcode657();
        System.out.println(leetcode657.judgeCircle("LL"));
    }
}
