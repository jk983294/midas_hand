package com.victor.utilities.algorithm.search;


import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class TopKHeap <T extends Comparable> {

    public int k, count;

    public T[] sorted;

    public Class cls;

    public TopKHeap(int k, Class cls) {
        this.k = k;
        this.cls = cls;
        this.count = 0;
        sorted = (T[]) Array.newInstance(cls, k);
    }

    public void clear(){
        for (int i = 0; i < count; i++) {
            sorted[i] = null;
        }
        count = 0;
    }

    public void add(T e){
        if(count < k)
            add(count, e);
        else
            removeAndInsert(e);
    }

    public T[] toArray(){
        return Arrays.copyOfRange(sorted, 0, count);
    }

    public List<T> toList(){
        return ArrayHelper.array2list(toArray());
    }

    /**
     * add element in position index, then adjust min heap
     * @param index - position in array
     * @param element - to put in that position
     */
    private void add(int index, T element) {
        int i = index;
        sorted[index] = element;
        T e = sorted[i];
        int parentIndex = ((i - 1) / 2);
        T parent = sorted[parentIndex];
        while (i > 0 && e.compareTo(parent) < 0) {
            MathHelper.swap(parentIndex, i, sorted);
            i = parentIndex;
            e = sorted[i];
            parentIndex = ((i - 1) / 2);
            parent = sorted[parentIndex];
        }
        ++count;
    }

    /**
     * check this element is bigger than the smallest in heap
     * if yes, it belong to top k, so remove the smallest in heap, insert it
     * then adjust heap
     */
    private void removeAndInsert(T element) {
        if ( element.compareTo(sorted[0]) > 0){
            sorted[0] = element;
            int i = 0; // index of the element being moved down the tree
            int left , right ;
            while (true) {
                left = (i * 2) + 1;
                right = left + 1;
                if (left >= k) // node has no left child
                    break;
                if (right >= k) { // node has a left child, but no right child
                    if (sorted[left].compareTo(sorted[i]) < 0)
                        MathHelper.swap(left, i, sorted); // if left child is greater than node
                    break;
                }
                T ithElement = sorted[i];
                T leftElement = sorted[left];
                T rightElement = sorted[right];
                if (ithElement.compareTo(leftElement) > 0) { // (left < i)
                    if (sorted[left].compareTo(rightElement) < 0) { // (left < right)
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
                if (rightElement.compareTo(ithElement) < 0) {
                    MathHelper.swap(right, i, sorted);
                    i = right;
                    continue;
                }
                // (n < left) & (n < right)
                break;
            }
        }
    }
}
