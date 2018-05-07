package com.victor.md.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class MpscQueue<T> {
    private final AtomicReferenceFieldUpdater<T, T> itemUpdater;
    private T head;
    private final AtomicReference<T> tail;

    public MpscQueue(AtomicReferenceFieldUpdater<T, T> itemUpdater) {
        this.itemUpdater = itemUpdater;
        this.tail = new AtomicReference<T>();
    }

    public final boolean put(T item) {
        //make sure the item is going to be put in the queue is not have next(), just a single item.
        assert (itemUpdater.get(item) == null);

        for (; ; ) {
            final T t = tail.get();
            // if the tail is still t, set the tail to item
            if (tail.compareAndSet(t, item)) {
                if (t == null) {
                    head = item;
                    return true; // empty queue, got one item
                } else {
                    itemUpdater.set(t, item); // set the t's (original tail) next value to item
                    return false;
                }
            }

        }
    }

    public T get() {
        if (head == null)
            return null;
        final T h = head;
        T next = itemUpdater.get(h);
        if (next == null) { // only one item in queue
            head = null; // set head to null
            if (tail.compareAndSet(h, null)) { // set tail to null
                return h;
            }
            while ((next = itemUpdater.get(h)) == null) ; // producer put new item
        }
        itemUpdater.lazySet(h, null); // cut the next item
        head = next;
        return h;
    }
}
