package com.victor.utilities.utils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

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

    public static boolean isEqual(double a, double b, double epsilon){
        return Math.abs(a - b) < epsilon;
    }

    public static boolean isZero(double v){
        return isEqual(v, 0d);
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
        T maxValue = array[0];
        for (T anArray : array) {
            if (maxValue.compareTo(anArray) < 0) {
                maxValue = anArray;
            }
        }
        return maxValue;
    }

    public static <T extends Comparable> T max(List<T> array) {
        T maxValue = array.get(0);
        for (T anArray : array) {
            if (maxValue.compareTo(anArray) < 0) {
                maxValue = anArray;
            }
        }
        return maxValue;
    }

    public static <T extends Comparable> T min(T[] array) {
        T minValue = array[0];
        for (T anArray : array) {
            if (minValue.compareTo(anArray) > 0) {
                minValue = anArray;
            }
        }
        return minValue;
    }

    public static <T extends Comparable> T min(List<T> array) {
        T minValue = array.get(0);
        for (T anArray : array) {
            if (minValue.compareTo(anArray) > 0) {
                minValue = anArray;
            }
        }
        return minValue;
    }

    public static double min(double... values){
        double minValue = values[0];
        for (double v : values) {
            minValue = Math.min(minValue, v);
        }
        return minValue;
    }

    public static double max(double... values){
        double maxValue = values[0];
        for (double v : values) {
            maxValue = Math.max(maxValue, v);
        }
        return maxValue;
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
        return !(x > 0 && y < 0) && !(x < 0 && y > 0);
    }

    public static boolean isSameSign(double x, double y) {
        return !(x > 0 && y < 0) && !(x < 0 && y > 0);
    }

    /**
     * strong constrain, if x or y is zero, consider not same sign
     */
    public static boolean isSameSignStrong(int x, int y) {
        return !(x == 0 || y == 0) && !(x > 0 && y < 0) && !(x < 0 && y > 0);
    }

    public static boolean isSameSignStrong(double x, double y) {
        return !(x == 0 || y == 0) && !(x > 0 && y < 0) && !(x < 0 && y > 0);
    }

    /**
     * range
     */
    public static boolean isInRange(double value, double low, double high){
        return value > Math.min(low, high) && value < Math.max(low, high);
    }

    public static boolean isInRange(int value, int low, int high){
        return value >= Math.min(low, high) && value <= Math.max(low, high);
    }

    /**check if [x, y] is in range of [a,b] */
    public static boolean isInRange(double x, double y, double a, double b){
        return isInRange(x, a, b) && isInRange(y, a, b);
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

    public static double[] multiplyInPlace(double[] x, double factor){
        for (int i = 0; i < x.length; i++) {
            x[i] *= factor;
        }
        return x;
    }

    public static double[] subtract(double[] a, double[] b){
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    public static Integer tryParse2Int(String str){
        if(str == null) return null;
        return Ints.tryParse(str);
    }

    public static Long tryParse2Long(String str){
        if(str == null) return null;
        return Longs.tryParse(str);
    }

    public static double log(double a, double b){
        return Math.log(b) / Math.log(a);
    }

    public static boolean isMoreAbs(double a, double b){
        return Math.abs(a) > Math.abs(b);
    }

    public static boolean isLessAbs(double a, double b){
        return Math.abs(a) < Math.abs(b);
    }

    public static boolean isMoreAbs(double a, double b, double factor){
        return Math.abs(a) > Math.abs(b) * factor;
    }

    public static boolean isLessAbs(double a, double b, double factor){
        return Math.abs(a) < Math.abs(b) * factor;
    }

}
