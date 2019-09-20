package com.xiao.framework.tree;

import javax.validation.constraints.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * 二叉搜索树(BST)。
 * 如果左子树非空，左子树上所有的结点的值小于根结点的值。右子树非空，那么右子树上的结点的值大于根结点的值。左右子树本身是二叉排序树。
 * 不支持插入多个compareValue相同的元素，如果相同，允许替换。
 *
 * 我在这个搜索二叉树中定义了comparator，这样用户可以根据自己的需求，自定义树结点进行比较的值。
 *
 * 该类型的树缺点在于，一旦插入时保持一定的顺序，那么最后产出的就是类似一个链表的结构。
 *
 * @author lix wang
 */
public class BinarySearchTree<T> implements Tree<T> {
    private TreeNode<T> root;
    private final Function<Object, Integer> comparator;

    public BinarySearchTree(final Function<Object, Integer> comparator) {
        this.comparator = comparator;
    }

    public BinarySearchTree(final T[] source, final Function<Object, Integer> comparator) {
        this.comparator = comparator;
        constructFromArray(source);
    }

    @Override
    public void constructFromArray(final Object[] source) {
        if (source == null || source.length <= 0) {
            return;
        }
        for (Object element : source) {
            putElement((T) element, false);
        }
    }

    /**
     * @param element the target element to create or to update.
     * @return origin element that was evicted.
     */
    @Override
    public T put (T element) {
        return putElement(element, true);
    }

    /**
     * @param element
     *
     * creation mode.
     */
    @Override
    public void add(T element) {
        putElement(element, false);
    }

    /**
     * get TreeNode according to compareValue.
     */
    @Override
    public TreeNode<T> get(Object element) {
        TreeNode<T> treeNode = getNode(element);
        if (treeNode != null && Objects.equals(getCompareValue(treeNode.getValue()), getCompareValue(element))) {
            return treeNode;
        }
        return null;
    }

    @Override
    public TreeNode<T> getRoot() {
        return this.root;
    }

    @Override
    public int getCompareValue(Object element) {
        if (element == null) {
            return 0;
        } else {
            return comparator.apply(element);
        }
    }

    /**
     * @param element the target need to be put.
     * @param evict if false I will create a new Node anyway.
     *
     * @return origin element.
     */
    private T putElement(T element, boolean evict) {
        T result = null;
        // if current tree is null, init a root.
        if (this.root == null) {
            this.root = new TreeNode<>(element);
        } else {
            TreeNode<T> node = getNode(element);
            int nodeCompareValue = getCompareValue(node.getValue());
            int elementCompareValue = getCompareValue(element);
            if (evict && Objects.equals(nodeCompareValue, elementCompareValue)) {
                result = node.getValue();
                node.setValue(element);
            } else {
                if (nodeCompareValue > elementCompareValue) {
                    insertNode(node, element, true);
                } else if (nodeCompareValue < elementCompareValue) {
                    insertNode(node, element, false);
                }
            }
        }
        return result;
    }

    private void insertNode(@NotNull TreeNode<T> root, T element, boolean left) {
        if (left) {
            TreeNode<T> originLeft = root.getLeft();
            TreeNode<T> newNode = new TreeNode<>(element);
            root.setLeft(newNode);
            newNode.setLeft(originLeft);
        } else {
            TreeNode<T> originRight = root.getRight();
            TreeNode<T> newNode = new TreeNode<>(element);
            root.setRight(newNode);
            newNode.setRight(originRight);
        }
    }
}
