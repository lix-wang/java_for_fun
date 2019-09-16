package com.xiao.framework.tree;

import java.util.function.Function;

/**
 * 平衡二叉树(AVL)。
 * 平衡二叉树左子树右子树的高度差最多是1。
 * 平衡二叉树主要是需要在插入后回溯判断有没有失衡结点，如果有需要判断进行单旋转还是双旋转。
 *
 * @author lix wang
 */
public class AVLTree<T> implements Tree<T> {
    private AVLTreeNode<T> root;
    private final Function<Object, Integer> comparator;

    public AVLTree(Function<Object, Integer> comparator) {
        this.comparator = comparator;
    }

    public AVLTree(Object[] source, Function<Object, Integer> comparator) {
        this.comparator = comparator;
    }

    public void constructFromArray(Object[] source) {
        if (source == null || source.length == 0) {
            return;
        }
        for (Object item : source) {
            putElement((T) item, false);
        }
    }

    @Override
    public T put(T element) {
        return null;
    }

    @Override
    public void add(T element) {

    }

    @Override
    public TreeNode<T> get(Object element) {
        return null;
    }

    private T putElement(T element, boolean evict) {
        return null;
    }

    private static class AVLTreeNode<T> extends TreeNode<T> {
        private int height;

        public AVLTreeNode() {
        }

        public AVLTreeNode(T value) {
            super(value);
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
