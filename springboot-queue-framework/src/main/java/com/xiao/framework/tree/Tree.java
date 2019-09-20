package com.xiao.framework.tree;

/**
 * Interface of tree.
 *
 * @author lix wang
 */
public interface Tree<T> {
    void constructFromArray(final Object[] source);

    T put (T element);

    void add(T element);

    TreeNode<T> get(Object element);

    TreeNode<T> getRoot();

    int getCompareValue(Object obj);

    /**
     * get TreeNode or parent TreeNode.
     */
    default TreeNode<T> getNode(Object element) {
        return getNode(getRoot(), element);
    }

    default TreeNode<T> getNode(TreeNode<T> node, Object element) {
        int rootVal, elementVal, leftVal = 0, rightVal = 0;
        if (node == null) {
            return null;
        } else if ((rootVal = getCompareValue(node.getValue())) >= (elementVal = getCompareValue(element))
                && (node.getLeft() == null || (leftVal = getCompareValue(node.getLeft().getValue())) < elementVal)) {
            return node;
        } else if (rootVal < elementVal
                && (node.getRight() == null || (rightVal = getCompareValue(node.getRight().getValue())) > elementVal)) {
            return node;
        } else if (rootVal >= elementVal && leftVal >= elementVal) {
            return getNode(node.getLeft(), element);
        } else if (rootVal < elementVal && rightVal <= elementVal) {
            return getNode(node.getRight(), element);
        }
        return null;
    }
}
