package com.victor.utilities.concurrent;

/**
 * test volatile is not safe
 * volatile only means very time it will refresh local variable with main memory
 * since the operation is not atomic, then it never safe
 */
public class VolatileTest {

    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    private static final int THREADS_COUNT = 5;

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        increase();
                    }
                }
            });
            threads[i].start();
        }

        // wait for all thread ends
        // TODO using correct sync method
        while (Thread.activeCount() > 1)
            Thread.yield();

        System.out.println(race);
    }

}
