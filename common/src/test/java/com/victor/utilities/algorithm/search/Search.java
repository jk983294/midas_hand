package com.victor.utilities.algorithm.search;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.victor.utilities.algorithm.search.BinarySearch;
import com.victor.utilities.algorithm.search.InterpolationSearch;
import com.victor.utilities.algorithm.search.LinearSearch;
import com.victor.utilities.algorithm.search.QuickSelect;

public class Search {

    private static final int           SIZE            = 9999;
    private static final int           offset          = 123;

    private static int[]               sorted          = new int[SIZE];
    static {
        for (int i = offset; i<offset+sorted.length; i++) {
            sorted[i-offset] = i;
        }
    }
    private static int                 valueIndex      = SIZE-(SIZE/4);
    private static int                 valueInArray    = sorted[valueIndex];
    private static int                 valueNotInArray = sorted[SIZE-1]+offset;

    @Test
    public void testBruteForceSearch() {
        int index = LinearSearch.find(valueInArray, sorted);
        assertTrue("Brute force error. expected="+valueIndex+" got="+index, (index==valueIndex));
        index = LinearSearch.find(valueNotInArray, sorted);
        assertTrue("Brute force error. expected="+Integer.MAX_VALUE+" got="+index, (index==Integer.MAX_VALUE));
    }

    @Test
    public void testInterpolationSearch() {
        int index = InterpolationSearch.find(valueInArray, sorted);
        assertTrue("Brute force error. expected="+valueIndex+" got="+index, (index==valueIndex));
        index = InterpolationSearch.find(valueNotInArray, sorted);
        assertTrue("Brute force error. expected="+Integer.MAX_VALUE+" got="+index, (index==Integer.MAX_VALUE));
    }

    @Test
    public void testQuickSelect() {
        int index = QuickSelect.find(valueInArray, sorted);
        assertTrue("Brute force error. expected="+valueIndex+" got="+index, (index==valueIndex));
        index = QuickSelect.find(valueNotInArray, sorted);
        assertTrue("Brute force error. expected="+Integer.MAX_VALUE+" got="+index, (index==Integer.MAX_VALUE));
    }
}
