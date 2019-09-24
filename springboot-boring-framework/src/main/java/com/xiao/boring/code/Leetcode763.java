package com.xiao.boring.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这道题采用贪心算法，先遍历找出每个字符最后出现的位置，然后从头扫描，找最优解。
 * 例如abbcab，首先片段位置为0 ~ 4，然后根据b，此时最优解为 0 ~ 5。
 *
 * 结果:Runtime: 7 ms, faster than 21.44% of Java online submissions for Partition Labels.
 * Memory Usage: 36.6 MB, less than 96.10% of Java online submissions for Partition Labels.
 *
 * @author lix wang
 */
public class Leetcode763 {
    public List<Integer> partitionLabels(String S) {
        List<Integer> result = new ArrayList<>();
        Map<Character, Integer> map = new HashMap<>();
        char[] charArray = S.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (map.get(charArray[i]) == null) {
                map.put(charArray[i], i);
            } else {
                if (i > map.get(charArray[i])) {
                    map.put(charArray[i], i);
                }
            }
        }
        int max = -1;
        while (true) {
            int index = max + 1;
            max = map.get(charArray[index]);
            for (int i = index; i <= max; i++) {
                Character c = charArray[i];
                if (map.get(c) > max) {
                    max = map.get(c);
                }
            }
            result.add(max - index + 1);
            if (max == charArray.length - 1) {
                break;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Leetcode763 leetcode763 = new Leetcode763();
        List<Integer> list = leetcode763.partitionLabels("ababcbacadefegdehijhklij");
        for (Integer len : list) {
            System.out.println(len + " ");
        }
    }
}
