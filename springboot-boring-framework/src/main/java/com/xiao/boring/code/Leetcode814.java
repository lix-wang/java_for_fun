package com.xiao.boring.code;

import com.xiao.boring.tree.TreeNode;

/**
 * 这个问题应该是后序遍历，遍历顺序左右根，如果左右子树都为null，且当前结点值为0，那么移除。
 *
 * 结果：Runtime: 0 ms, faster than 100.00% of Java online submissions for Binary Tree Pruning.
 * Memory Usage: 34.4 MB, less than 100.00% of Java online submissions for Binary Tree Pruning.
 *
 * @author lix wang
 */
public class Leetcode814 {
    public TreeNode pruneTree(TreeNode root) {
        if (root != null) {
            root.left = pruneTree(root.left);
            root.right = pruneTree(root.right);
            if (root.left == null && root.right == null && root.val == 0) {
                root = null;
            }
        }
        return root;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(0);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(1);
        Leetcode814 leetcode814 = new Leetcode814();
        TreeNode node = leetcode814.pruneTree(root);
        System.out.println(node);
    }
}
