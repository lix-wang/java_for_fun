package com.xiao.framework.base.collection.util;

/**
 * For range check
 *
 * @author lix wang
 */
public class RangeCheckUtils {
    /**
     * 检查 0 <= index < size
     *
     * @param index
     * @param size
     */
    public static void checkRange(final int index, final int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index, size));
        }
    }

    /**
     * 检查 0 <= index <= size
     *
     * @param index
     * @param size
     */
    public static void rangeCheckForAdd(final int index, final int size) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index, size));
        }
    }

    /**
     * 检查 0 <= fromIndex <= toIndex <= size
     *
     * @param fromIndex
     * @param toIndex
     * @param size
     */
    public static void checkRange(final int fromIndex, final int toIndex, final int size) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(illegalRangeArguments(fromIndex, toIndex));
        }
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(fromIndex, size));
        }
        if (toIndex > size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(toIndex, size));
        }
    }

    public static void checkRange(boolean checkResult, String message) {
        if (!checkResult) {
            throw new IllegalArgumentException(message);
        }
    }

    private static String illegalRangeArguments(final int fromIndex, final int toIndex) {
        return String.format("fromIndex(%d) > toIndex(%d)", fromIndex, toIndex);
    }

    private static String outOfBoundsMsg(final int index, final int size) {
        return "Index: " + index + ", Size: " + size;
    }
}
