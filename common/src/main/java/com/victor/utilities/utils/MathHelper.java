package com.victor.utilities.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Math Utils
 */
public class MathHelper {

    private final static Random random = new Random();

    public static int multiplyReturnInt(double a, double b){
        return Double.valueOf(a * b).intValue();
    }

    public static int multiplyReturnInt(int a, double b){
        return Double.valueOf(a * b).intValue();
    }

    public static int multiplyReturnInt(double a, int b){
        return Double.valueOf(a * b).intValue();
    }

    public static boolean isEqual(double a, double b){
        return Math.abs(a - b) < 1e-6;
    }

    /**
     * generate uniform random variables in given bounds
     */
    public static double[] randomRange(double[] low, double[] up){
        int len = up.length;
        double[] rand = new double[len];
        for (int i = 0; i < len; i++) {
            rand[i] = randomRange(low[i], up[i]);
        }
        return rand;
    }

    public static double randomRange(double low, double up){
        return (up - low) * random.nextDouble() + low;
    }

    public static List<double[]> randomRangeList(int size, double[] low, double[] up){
        List<double[]> popList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            popList.add(randomRange(low, up));
        }
        return popList;
    }

    /**
     * max and min
     */
    public static <T extends Comparable> T max(T[] array) {
        T maxvalue = array[0];
        for (int i = 0; i < array.length; i++) {
            if (maxvalue.compareTo(array[i]) < 0){
                maxvalue = array[i];
            }
        }
        return maxvalue;
    }

    public static <T extends Comparable> T max(List<T> array) {
        T maxvalue = array.get(0);
        for (int i = 0; i < array.size(); i++) {
            if (maxvalue.compareTo(array.get(i)) < 0){
                maxvalue = array.get(i);
            }
        }
        return maxvalue;
    }

    public static <T extends Comparable> T min(T[] array) {
        T minvalue = array[0];
        for (int i = 0; i < array.length; i++) {
            if (minvalue.compareTo(array[i]) > 0){
                minvalue = array[i];
            }
        }
        return minvalue;
    }

    public static <T extends Comparable> T min(List<T> array) {
        T minvalue = array.get(0);
        for (int i = 0; i < array.size(); i++) {
            if (minvalue.compareTo(array.get(i)) > 0){
                minvalue = array.get(i);
            }
        }
        return minvalue;
    }


    /**
     * swap elements in array
     */
    public static <T> void swap(int parentIndex, int childIndex, T[] array) {
        T parent = array[parentIndex];
        array[parentIndex] = array[childIndex];
        array[childIndex] = parent;
    }

    public static void swap(int parentIndex, int childIndex, double[] array) {
        double parent = array[parentIndex];
        array[parentIndex] = array[childIndex];
        array[childIndex] = parent;
    }

    public static boolean isSameSign(int x, int y) {
        if( x > 0 && y < 0) return false;
        else if( x < 0 && y > 0) return false;
        else return true;
    }

    public static boolean isSameSign(double x, double y) {
        if( x > 0 && y < 0) return false;
        else if( x < 0 && y > 0) return false;
        else return true;
    }

    /**
     * strong constrain, if x or y is zero, consider not same sign
     */
    public static boolean isSameSignStrong(int x, int y) {
        if(x == 0 || y == 0) return false;
        if( x > 0 && y < 0) return false;
        else if( x < 0 && y > 0) return false;
        else return true;
    }

    public static boolean isSameSignStrong(double x, double y) {
        if(x == 0 || y == 0) return false;
        if( x > 0 && y < 0) return false;
        else if( x < 0 && y > 0) return false;
        else return true;
    }

    /**
     * range
     */
    public static boolean isInRange(double value, double low, double high){
        if(value > Math.min(low, high) && value < Math.max(low, high)) return true;
        else return false;
    }

    public static boolean isInRange(int value, int low, int high){
        if(value >= Math.min(low, high) && value <= Math.max(low, high)) return true;
        else return false;
    }

    /**check if [x, y] is in range of [a,b] */
    public static boolean isInRange(double x, double y, double a, double b){
        if(isInRange(x, a, b) && isInRange(y, a, b) ) return true;
        else return false;
    }

    public static double average(double x, double y){
        return (x + y) / 2;
    }

    public static double[] log(double[] x){
        int len = x.length;
        double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            result[i] = Math.log(x[i]);
        }
        return result;
    }

    public static double[] subtract(double[] a, double[] b){
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

}
