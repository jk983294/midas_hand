package com.victor.utilities.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.MathArrays;

import java.lang.reflect.Array;
import java.util.*;

/**
 * array helper
 */
public class ArrayHelper {

    /**
     * copy double array
     */
    public static double[] copy(double[] original){
        return MathArrays.copyOf(original);
    }

    public static double[] copy(double[] original, int len){
        return MathArrays.copyOf(original, len);
    }

    public static double[] copy(double[] original, int from, int len){
        return Arrays.copyOfRange(original, from, from + len);
    }

    public static double[] copyToNewLenArray(double[] original, int newLength){
        if(original == null ) return new double[newLength];
        double[] copy = new double[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }


    /**
     * transform between list and array
     */
    public static <T> T[] list2array(List<T> original){
        T[] array = (T[]) Array.newInstance(original.get(0).getClass(), original.size());
        for (int i = 0; i < array.length; i++) {
            array[i] = original.get(i);
        }
        return array;
    }

    public static <T> List<T> array2list(T[] original){
        List<T> list = new ArrayList<>();
        for (int i = 0; i < original.length; i++) {
            list.add(original[i]);
        }
        return list;
    }

    public static <T> List<T> array2list(Collection<T> original){
        List<T> list = new ArrayList<>(original);
        return list;
    }

    public static <T> boolean isNull(T[] original){
        if(original == null || original.length == 0) return true;
        else return false;
    }

    public static boolean isNull(double[] original){
        if(original == null || original.length == 0) return true;
        else return false;
    }

    public static boolean isNull(int[] original){
        if(original == null || original.length == 0) return true;
        else return false;
    }

    public static <T> boolean isNull(List<T> original){
        if(original == null || original.size() == 0) return true;
        else return false;
    }


    /**
     * build new array given initial value
     */
    public static double[] buildArray(int lens, double initialValue ) {
        double[] array = new double[lens];
        for (int i = 0; i < lens; i++) {
            array[i] = initialValue;
        }
        return array;
    }

    public static int[] buildArray(int lens, int initialValue ) {
        int[] array = new int[lens];
        for (int i = 0; i < lens; i++) {
            array[i] = initialValue;
        }
        return array;
    }

    public static void fill(int[] array, int from, int to, int value) {
        for (int i = from; i < to; i++) {
            array[i] = value;
        }
    }

    /**calculate helper*/
    public static void ebeSubtractInplace(double[] a, double[] b, double[] result){
        for (int i = 0; i < a.length; i++) {
            result[i] -= b[i];
        }
    }

    public static <T> boolean containAny(Set<T> bigSet, Set<T> smallSet){
        for(T data : smallSet){
            if(bigSet.contains(data)){
                return true;
            }
        }
        return false;
    }

    public static <T> T get(List<T> data, int index){
        if(CollectionUtils.isNotEmpty(data) && data.size() > index){
            return data.get(index);
        }
        return null;
    }

}
