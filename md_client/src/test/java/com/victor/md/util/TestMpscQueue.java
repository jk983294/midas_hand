package com.victor.md.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static org.junit.Assert.assertEquals;

public class TestMpscQueue {
    class Item {
        public volatile Item nextItem;
        public final int value;

        public Item(int value) {
            this.value = value;
        }
    }

    class ProducerThread extends Thread {
        private int start;
        private int end;

        public ProducerThread(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                Item item = new Item(i);
                queue.put(item);
            }
        }
    }

    class ConsumerThread extends Thread {
        public ConsumerThread() {
        }

        @Override
        public void run() {
            int counter = 0;
            do {
                Item item = queue.get();
                if (item != null) {
                    counter++;
                    array[item.value] = item.value;
                }
            } while (counter < 300);
            assertEquals(queue.get(), null);  //only can get 300 items
        }
    }

    private MpscQueue<Item> queue;
    private int[] array;

    @Test
    public void testMpscQueue() throws InterruptedException {
        this.queue = new MpscQueue<>(AtomicReferenceFieldUpdater.newUpdater(Item.class, Item.class, "nextItem"));
        this.array = new int[300];
        ProducerThread thread1 = new ProducerThread(0, 100);
        ProducerThread thread2 = new ProducerThread(100, 200);
        ProducerThread thread3 = new ProducerThread(200, 300);
        thread1.start();
        thread2.start();
        thread3.start();

        ConsumerThread readThread = new ConsumerThread();
        readThread.start();

        thread1.join();
        thread2.join();
        thread3.join();
        readThread.join();

        for (int i = 0; i < 300; i++) {
            assertEquals(array[i], i); // get every item once
        }
    }

}
