package com.victor.md.util;

import java.util.Objects;

@FunctionalInterface
public interface ObjectShortObjectConsumer<T, V> {
    public void accept(T t, short u, V v);

    public default ObjectShortObjectConsumer<T, V> andThen(ObjectShortObjectConsumer<? super T, ? super V> after) {
        Objects.requireNonNull(after);
        return (a, b, c) ->
        {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
