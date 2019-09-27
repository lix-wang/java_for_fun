package com.xiao.framework.base.collection;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * From {@link Iterable}
 * <p>
 * 重复造轮子，只是为了理解集合框架机制。
 * 底层的Iterable接口，标识实现了该接口的对象都具有迭代器功能。
 * <p>
 * 由于增强for循环语法糖只能用于实现了Iterable的接口和数组，
 * 因此这里我们自定义的LixIterable接口也继承自Iterable。
 * <p>
 * {@link LixCollection} 的父类接口。
 */
public interface LixIterable<T> extends Iterable<T> {
    /**
     * 返回实现了该接口的集合的迭代器。
     *
     * @return {@link LixIterator}
     */
    @NotNull
    LixIterator<T> getIterator();

    default Iterator<T> iterator() {
        return getIterator();
    }

    /**
     * 经常会使用该方法来遍历处理，采用函数式接口，传入一个lambda即可。
     * 原始的遍历处理采用增强for循环，这里使用原始的for循环遍历。
     * 这里采用super指定泛型的超类型限定，来确保一定能执行该action。
     * 例如：
     * <p>
     *      lolHeroNames.forEach(System.out::println);
     *
     * @param action 遍历元素执行相应的行为。
     */
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (LixIterator<T> iterator = getIterator(); iterator.hasNext(); ) {
            action.accept(iterator.next());
        }
    }
}
