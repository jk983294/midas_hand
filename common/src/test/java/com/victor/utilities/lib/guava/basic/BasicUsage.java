package com.victor.utilities.lib.guava.basic;


import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * demo for basic utility
 */
public class BasicUsage {

    /**
     * avoid null value
     */
    public static void optional(){
        Optional<Integer> possible = Optional.of(5);
        possible.isPresent(); // returns true
        possible.get(); // returns 5
    }

    public static void preconditions(){
        int i = 0, j = 2;
        checkArgument(i >= 0, "Argument was %s but expected nonnegative", i);
        checkArgument(i < j, "Expected i < j, but %s > %s", i, j);
    }

    public static void main(String[] args) {
        optional();
        preconditions();
    }
}
