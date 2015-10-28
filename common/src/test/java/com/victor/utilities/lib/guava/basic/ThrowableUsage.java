package com.victor.utilities.lib.guava.basic;

import com.google.common.base.Throwables;

import java.io.IOException;

/**
 * throwable usage demo
 */
public class ThrowableUsage {

    public static void main(String[] args) {
        try {
            showcaseThrowables();
        } catch (InvalidInputException e) {
            //get the root cause
            System.out.println(Throwables.getRootCause(e));
        }catch (Exception e) {
            //get the stack trace in string format
            System.out.println(Throwables.getStackTraceAsString(e));
        }

        try {
            showcaseThrowables1();
        }catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    public static void showcaseThrowables() throws InvalidInputException{
        try {
            sqrt(-3.0);
        } catch (Throwable e) {
            //check the type of exception and throw it
            Throwables.propagateIfInstanceOf(e, InvalidInputException.class);
            Throwables.propagate(e);
        }
    }

    public static void showcaseThrowables1(){
        try {
            int[] data = {1,2,3};
            getValue(data, 4);
        } catch (Throwable e) {
            Throwables.propagateIfInstanceOf(e, IndexOutOfBoundsException.class);
            Throwables.propagate(e);
        }
    }

    public static double sqrt(double input) throws InvalidInputException{
        if(input < 0) throw new InvalidInputException();
        return Math.sqrt(input);
    }

    public static double getValue(int[] list, int index) throws IndexOutOfBoundsException {
        return list[index];
    }

    public static void dummyIO() throws IOException {
        throw new IOException();
    }

    public static class InvalidInputException extends Exception {
    }
}
