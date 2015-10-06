package com.victor.utilities.utils;

import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * unit test for DateHelper
 */
public class DateHelperTest {

    @Test
    public void toDateTest() throws ParseException {
        Date now = new Date();
        int date2int = DateHelper.toDate(now);
        Date int2date = DateHelper.toDate(date2int);
        VisualAssist.print("current date", now);
        VisualAssist.print("current date convert to int", date2int);
        VisualAssist.print("current date int convert back to Date", int2date);
    }
}
