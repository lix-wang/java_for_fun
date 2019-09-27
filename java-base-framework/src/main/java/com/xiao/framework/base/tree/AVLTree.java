package com.xiao.framework.base.tree;

import javax.validation.constraints.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * 平衡二叉树(AVL)。
 * 平衡二叉树左子树右子树的高度差最多是1。
 * 平衡二叉树主要是需要在插入后回溯判断有没有失衡结点，如果有需要判断进行单旋转还是双旋转。
 * 不支持插入多个compareValue相同的元素，如果相同支持替换。
 *
 * AVL缺点在于，插入时时间复杂度为O(1)，但删除结点失衡后，需要维护从删除结点到根结点之间所有结点的平衡，旋转量级为O(longN)。
 *
 * @author lix wang
 */
public class AVLTree<T> implements Tree<T> {
    private AVLTreeNode<T> root;
    private T lastEvictedElement;
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
        return putElement(element, true);
    }

    @Override
    public void add(T element) {
        putElement(element, false);
    }

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
    public int getCompareValue(Object obj) {
        if (obj == null) {
            return 0;
        }
        return comparator.apply(obj);
    }

    private T putElement(T element, boolean evict) {
        this.root = putElement(this.root, element, evict);
        T result = this.lastEvictedElement;
        this.lastEvictedElement = null;
        return result;
    }

    private AVLTreeNode<T> putElement(AVLTreeNode<T> node, T element, boolean evict) {
        int elementVal, nodeVal, leftVal = 0, rightVal = 0;
        if (node == null) {
            return new AVLTreeNode<>(element);
        } else if ((elementVal = getCompareValue(element)) > (nodeVal = getCompareValue(node.getValue()))) {
            node.setRight(putElement((AVLTreeNode<T>) node.getRight(), element, evict));
        } else if (elementVal < nodeVal) {
            node.setLeft(putElement((AVLTreeNode<T>) node.getLeft(), element, evict));
        } else {
            if (evict) {
                this.lastEvictedElement = node.getValue();
                node.setValue(element);
            }
            return node;
        }
        node.setHeight(1 + Math.max(height((AVLTreeNode<T>) node.getLeft()), height((AVLTreeNode<T>) node.getRight())));
        int balance = getNodeBalance(node);
        // left left
        if (balance > 1 && elementVal < (leftVal = getCompareValue(node.getLeft().getValue()))) {
            return rightRotate(node);
        }
        // right right
        if (balance < -1 && elementVal > (rightVal = getCompareValue(node.getRight().getValue()))) {
            return leftRotate(node);
        }
        // left right
        if (balance > 1 && elementVal > leftVal) {
            node.setLeft(leftRotate((AVLTreeNode<T>) node.getLeft()));
            return rightRotate(node);
        }
        // right left
        if (balance < -1 && elementVal < rightVal) {
            node.setRight(rightRotate((AVLTreeNode<T>) node.getRight()));
            return leftRotate(node);
        }
        return node;
    }

    private AVLTreeNode<T> leftRotate(@NotNull AVLTreeNode<T> node) {
        AVLTreeNode<T> originRightNode = (AVLTreeNode<T>) node.getRight();
        AVLTreeNode<T> newRightNode = (AVLTreeNode<T>) originRightNode.getLeft();
        originRightNode.setLeft(node);
        node.setRight(newRightNode);
        node.setHeight(Math.max(height((AVLTreeNode<T>) node.getLeft()), height((AVLTreeNode<T>) node.getRight())) + 1);
        originRightNode.setHeight(Math.max(height((AVLTreeNode<T>) originRightNode.getLeft()),
                height((AVLTreeNode<T>) originRightNode.getRight())) + 1);
        return originRightNode;
    }

    private AVLTreeNode<T> rightRotate(@NotNull AVLTreeNode<T> node) {
        AVLTreeNode<T> originLeftNode = (AVLTreeNode<T>) node.getLeft();
        AVLTreeNode<T> newLeftNode = (AVLTreeNode<T>) originLeftNode.getRight();
        originLeftNode.setRight(node);
        node.setLeft(newLeftNode);
        node.setHeight(Math.max(height((AVLTreeNode<T>) node.getLeft()), height((AVLTreeNode<T>) node.getRight())) + 1);
        originLeftNode.setHeight(Math.max(height((AVLTreeNode<T>) originLeftNode.getLeft()),
                height((AVLTreeNode<T>) originLeftNode.getRight())) + 1);
        return originLeftNode;
    }

    private int getNodeBalance(AVLTreeNode<T> node) {
        if (node == null) {
            return 0;
        }
        return height((AVLTreeNode<T>) node.getLeft()) - height((AVLTreeNode<T>) node.getRight());
    }

    private int height(AVLTreeNode<T> node) {
        if (node == null) {
            return 0;
        }
        return node.getHeight();
    }

    private static class AVLTreeNode<T> extends TreeNode<T> {
        private int height = 1;

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
