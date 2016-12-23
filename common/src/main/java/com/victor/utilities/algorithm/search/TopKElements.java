package com.victor.utilities.algorithm.search;

import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * find first k biggest elements
 */
public class TopKElements  {

    public static <T extends Comparable> T[] getFirstK(List<T> unsorted, int k){
        if(CollectionUtils.isEmpty(unsorted)) return null;
        T[] array = ArrayHelper.list2array(unsorted);
        return getFirstK(array, k);
    }

    public static <T extends Comparable> T[] getFirstK(T[] unsorted, int k){
        if ( k > unsorted.length){
            return unsorted;
        } else if ( (double) k / unsorted.length < 0.4 ){
            return getFirstKUsingHeap(unsorted, k);
        } else {
            return getFirstKUsingQuickSort(unsorted, k);
        }
    }

    public static <T extends Comparable> T[] getFirstKUsingHeap(T[] unsorted, int k) {
        TopKHeap<T> topKHeap = new TopKHeap<>(k, unsorted[0].getClass());
        for(T e : unsorted){
            topKHeap.add(e);
        }
        return topKHeap.toArray();
    }

    /**
     * partition based on pivot
     * @return pivot position
     */
    private static <T extends Comparable<T>> int partition(T arr[], int left, int right)
    {
        int i = left, j = right;
        T pivot = arr[(left + right) / 2];

        while (i <= j) {
            while (arr[i].compareTo(pivot) > 0)
                i++;
            while (arr[j].compareTo(pivot) < 0)
                j--;
            if (i <= j) {
                MathHelper.swap(i, j, arr);
                i++;
                j--;
            }
        }
        return i;
    }

    /**
     * reverse order, from big to small
     */
    private static <T extends Comparable<T>> void quickSort(T arr[], int left, int right, int k) {
        int index = partition(arr, left, right);
        if ( index + 1 < k){
            if (index < right)
                quickSort(arr, index, right, k);
        } else if ( index + 1 > k){
            if (left < index - 1)
                quickSort(arr, left, index - 1, k);
        }
    }

    public static <T extends Comparable> T[] getFirstKUsingQuickSort(T[] unsorted, int k) {
        quickSort(unsorted, 0, unsorted.length - 1, k);
        return Arrays.copyOfRange(unsorted, 0, k);
    }


    /**
     * special for double
     */
    public static double[] getFirstK(double[] unsorted, int k){
        if ( k > unsorted.length){
            return unsorted;
        } else if ( (double) k / unsorted.length < 0.4 ){
            return getFirstKUsingHeap(unsorted, k);
        } else {
            return getFirstKUsingQuickSort(unsorted, k);
        }
    }

    public static double[] getFirstKUsingHeap(double[] unsorted, int k) {
        double[] sorted = new double[k];
        int length = unsorted.length;
        for (int i = 0; i < k; i++) {
            add(i, unsorted[i], sorted);
        }
        for (int i = k; i < length; i++) {
            removeAndInsert(unsorted[i], sorted, k);
        }
        return sorted;
    }

    /**
     * add element in position index, then adjust min heap
     * @param index - position in array
     * @param element - to put in that position
     */
    private static void add(int index, double element, double[] sorted) {
        int i = index;
        sorted[index] = element;
        double e = sorted[i];
        int parentIndex = ((i - 1) / 2);
        double parent = sorted[parentIndex];
        while (i > 0 && e < parent) {
            MathHelper.swap(parentIndex, i, sorted);
            i = parentIndex;
            e = sorted[i];
            parentIndex = ((i - 1) / 2);
            parent = sorted[parentIndex];
        }
    }

    /**
     * check this element is bigger than the smallest in heap
     * if yes, it belong to top k, so remove the smallest in heap, insert it
     * then adjust heap
     */
    private static void removeAndInsert(double element, double[] sorted, int k) {
        if ( element > sorted[0]){
            sorted[0] = element;
            int i = 0; // index of the element being moved down the tree
            int left , right ;
            while (true) {
                left = (i * 2) + 1;
                right = left + 1;
                if (left >= k) // node has no left child
                    break;
                if (right >= k) { // node has a left child, but no right child
                    if (sorted[left] < sorted[i])
                        MathHelper.swap(left, i, sorted); // if left child is greater than node
                    break;
                }
                double ithElement = sorted[i];
                double leftElement = sorted[left];
                double rightElement = sorted[right];
                if (ithElement > leftElement) { // (left < i)
                    if (sorted[left] < rightElement) { // (left < right)
                        MathHelper.swap(left, i, sorted);
                        i = left;
                        continue;
                    }
                    // (left < i)
                    MathHelper.swap(right, i, sorted);
                    i = right;
                    continue;
                }
                // (i < left)
                if (rightElement < ithElement) {
                    MathHelper.swap(right, i, sorted);
                    i = right;
                    continue;
                }
                // (n < left) & (n < right)
                break;
            }
        }
    }

    /**
     * partition based on pivot
     * @return pivot position
     */
    private static int partition(double arr[], int left, int right)
    {
        int i = left, j = right;
        double pivot = arr[(left + right) / 2];

        while (i <= j) {
            while (arr[i] > pivot)
                i++;
            while (arr[j] < pivot)
                j--;
            if (i <= j) {
                MathHelper.swap(i, j, arr);
                i++;
                j--;
            }
        }
        return i;
    }

    /**
     * reverse order, from big to small
     */
    private static void quickSort(double arr[], int left, int right, int k) {
        int index = partition(arr, left, right);
        if ( index + 1 < k){
            if (index < right)
                quickSort(arr, index, right, k);
        } else if ( index + 1 > k){
            if (left < index - 1)
                quickSort(arr, left, index - 1, k);
        }
    }

    public static double[] getFirstKUsingQuickSort(double[] unsorted, int k) {
        quickSort(unsorted, 0, unsorted.length - 1, k);
        return Arrays.copyOfRange(unsorted, 0, k);
    }

}
