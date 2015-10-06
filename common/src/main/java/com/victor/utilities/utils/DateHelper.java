package com.victor.utilities.utils;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * deal with date conversion
 */
public class DateHelper {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static Date toDate(int date) throws ParseException {
        return sdf.parse(new StringBuilder().append(date).toString());
    }

    public static int toDate(Date date){
        return Integer.valueOf(sdf.format(date));
    }

    public static Date[] toDates(int[] date) throws ParseException {
        Date[] dates = new Date[date.length];
        for (int i = 0; i < date.length; i++) {
            dates[i] = toDate(date[i]);
        }
        return dates;
    }

    public static int[] toDates(Date[] date){
        int[] dates = new int[date.length];
        for (int i = 0; i < date.length; i++) {
            dates[i] = toDate(date[i]);
        }
        return dates;
    }
}
