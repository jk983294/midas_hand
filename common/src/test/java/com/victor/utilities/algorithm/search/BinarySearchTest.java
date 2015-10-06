package com.victor.utilities.algorithm.search;

import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BinarySearchTest {

    @Test
    public void testBinarySearchTest() {
        int[] array = new int[]{ 1, 2, 4, 5, 6, 7, 9, 24, 30};
        VisualAssist.print(BinarySearch.find( -1, array));
        VisualAssist.print(BinarySearch.find( 2, array));
        VisualAssist.print(BinarySearch.find( 3, array));
        VisualAssist.print(BinarySearch.find( 25, array));
        VisualAssist.print(BinarySearch.find( 30, array));
        VisualAssist.print(BinarySearch.find( 33, array));
    }
}
