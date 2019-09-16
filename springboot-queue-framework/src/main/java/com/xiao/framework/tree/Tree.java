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
}
