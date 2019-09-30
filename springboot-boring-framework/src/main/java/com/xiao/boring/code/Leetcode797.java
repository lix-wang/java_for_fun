package com.xiao.boring.code;

import java.util.ArrayList;
import java.util.List;

/**
 * 这道题开始的时候根本没看懂，leetcode 很多题描述都很迷。。。。。。
 * 二维数组中，i-1位置中的数组，就是对应于第i个结点相邻的有向结点。例如 0 结点，对应相邻结点为[1, 2]。
 * 求从 0 到 N - 1 的路径条数，根据描述就是求0 - 3 路径。由于是有向图，不会重复，这里直接递归，符合当前结点为3，那么就是一条路。
 *
 * 结果：Runtime: 2 ms, faster than 99.53% of Java online submissions for All Paths From Source to Target.
 * Memory Usage: 38.7 MB, less than 100.00% of Java online submissions for All Paths From Source to Target.
 *
 * @author lix wang
 */
public class Leetcode797 {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();
        allPathsSourceTarget(graph, 0, null, result);
        return result;
    }

    private void allPathsSourceTarget(int[][] graph, int index, List<Integer> road, List<List<Integer>> allRoads) {
        if (index == graph.length - 1) {
            allRoads.add(road);
        }
        for (int item : graph[index]) {
            List<Integer> currentRoad;
            if (index == 0) {
                currentRoad = new ArrayList<>();
                currentRoad.add(0);
            } else {
                currentRoad = new ArrayList<>(road);
            }
            currentRoad.add(item);
            allPathsSourceTarget(graph, item, currentRoad, allRoads);
        }
    }

    public static void main(String[] args) {
        Leetcode797 leetcode797 = new Leetcode797();
        List<List<Integer>> result = leetcode797.allPathsSourceTarget(
                new int[][] {new int[] {1, 2}, new int[]{3}, new int[]{3}, new int[]{}});
        for (List<Integer> current : result) {
            System.out.println(current.toString());
        }
    }
}
