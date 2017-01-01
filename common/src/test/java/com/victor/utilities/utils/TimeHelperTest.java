package com.victor.utilities.utils;


import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeHelperTest {

    @Test
    public void testIsSameWeek(){
        final DateTime d1 = new DateTime(2014, 12, 31, 0, 0);
        final DateTime d2 = new DateTime(2015, 1, 1, 0, 0);
        final DateTime d3 = new DateTime(2015, 1, 2, 0, 0);
        final DateTime d4 = new DateTime(2015, 1, 8, 0, 0);

        assertTrue(TimeHelper.isSameWeek(d1, d2));
        assertTrue(TimeHelper.isSameWeek(d2, d1));

        assertTrue(TimeHelper.isSameWeek(d2, d3));
        assertTrue(TimeHelper.isSameWeek(d3, d2));

        assertFalse(TimeHelper.isSameWeek(d2, d4));
        assertFalse(TimeHelper.isSameWeek(d4, d2));

        assertFalse(TimeHelper.isSameWeek(d1, d4));
        assertFalse(TimeHelper.isSameWeek(d4, d1));


    }

}
