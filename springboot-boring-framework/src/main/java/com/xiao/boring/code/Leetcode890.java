package com.xiao.boring.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个题创建两个映射散列，然后校验散列就可以判断。根据题目可以知道字符串长度 1 <= length <= 20，HashMap默认大小为16，
 * 所以我初始化阶段直接给定初始长度的散列，能避免内存拓展导致的时间空间的消耗。而且重复利用散列能避免多次创建对象引起的内存消耗。
 *
 * 结果：Runtime: 2 ms, faster than 80.45% of Java online submissions for Find and Replace Pattern.
 * Memory Usage: 36.5 MB, less than 100.00% of Java online submissions for Find and Replace Pattern.
 *
 * @author lix wang
 */
public class Leetcode890 {
    public List<String> findAndReplacePattern(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        int initSize = (int) (pattern.length() / 0.75f + 1);
        Map<Character, Character> characterMap = new HashMap<>(initSize);
        Map<Character, Character> patternMap = new HashMap<>(initSize);
        for (String word : words) {
            boolean addFlag = true;
            for (int i = 0; i < word.length(); i++) {
                if (characterMap.get(word.charAt(i)) == null && patternMap.get(pattern.charAt(i)) == null) {
                    characterMap.put(word.charAt(i), pattern.charAt(i));
                    patternMap.put(pattern.charAt(i), word.charAt(i));
                } else {
                    Character patternMapping = characterMap.get(word.charAt(i));
                    Character wordMapping = patternMap.get(pattern.charAt(i));
                    if (patternMapping == null || wordMapping == null || patternMapping != pattern.charAt(i)
                            || wordMapping != word.charAt(i)) {
                        addFlag = false;
                        break;
                    }
                }
            }
            characterMap.clear();
            patternMap.clear();
            if (addFlag) {
                result.add(word);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Leetcode890 leetcode890 = new Leetcode890();
        List<String> result = leetcode890.findAndReplacePattern(new String[] {"abc","deq","mee","aqq","dkd","ccc"},
                "abb");
        for (String str : result) {
            System.out.println(str + " ");
        }
    }
}
