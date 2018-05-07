package com.victor.md.util;

import java.util.Objects;

@FunctionalInterface
public interface ObjectShortConsumer<T> {
    public void accept(T t, short u);

    public default ObjectShortConsumer<T> andThen(ObjectShortConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (a, b) ->
        {
            accept(a, b);
            after.accept(a, b);
        };
    }
}
