package com.victor.utilities.algorithm.search;

public class BinarySearch {

    /**
     * Assuming the array is sorted
     * if not found, return the up bound, but not exceed the array length
     */
    public static final int find(int searchElement, int[] array) {
        int len = array.length - 1;
        int minIndex = 0, maxIndex = len, currentIndex = 0;
        while (minIndex <= maxIndex) {
            currentIndex = (minIndex + maxIndex) / 2 ;
            if (array[currentIndex] < searchElement) {
                minIndex = currentIndex + 1;
            }
            else if (array[currentIndex] > searchElement) {
                maxIndex = currentIndex - 1;
            }
            else {
                return currentIndex;
            }
        }
        return ( minIndex > len) ? -len : -minIndex;
    }
}
