package com.xiao.framework.tree;

import java.util.function.Function;

/**
 * Usage demo.
 *
 * @author lix wang
 */
public class UsageDemo {
    public static void main(String[] args) {
        Integer[] ary = new Integer[] {1, 234, 54, 6, 2, 4, 6, 24, 656, 76};
        BinarySearchTree<Integer> tree = new BinarySearchTree(Function.identity());
        tree.constructFromArray(ary);
        System.out.println(tree);
        AVLTree<Integer> avlTree = new AVLTree(Function.identity());
        avlTree.constructFromArray(ary);
        System.out.println(avlTree);
    }
}
