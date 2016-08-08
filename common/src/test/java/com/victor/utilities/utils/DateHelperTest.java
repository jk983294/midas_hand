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
        int date2int = TimeHelper.date2cob(now);
        Date int2date = TimeHelper.cob2date(date2int);
        VisualAssist.print("current date", now);
        VisualAssist.print("current date convert to int", date2int);
        VisualAssist.print("current date int convert back to Date", int2date);
    }

    @Test
    public void month2dateTest() throws ParseException {
        Date now = new Date();
        int cob = TimeHelper.date2cob(now);
        int monthInt = TimeHelper.cob2month(cob);
        Date int2date = TimeHelper.month2date(monthInt);
        VisualAssist.print("current date", now);
        VisualAssist.print("current date convert to int", monthInt);
        VisualAssist.print("current date int convert back to Date", int2date);
    }
}
