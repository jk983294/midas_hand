package com.victor.utilities.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * deal with date conversion
 */
public class TimeHelper {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static Date cob2date(int date) throws ParseException {
        return sdf.parse(new StringBuilder().append(date).toString());
    }

    public static int date2cob(Date date){
        return Integer.valueOf(sdf.format(date));
    }

    public static Date[] cob2date(int[] date) throws ParseException {
        Date[] dates = new Date[date.length];
        for (int i = 0; i < date.length; i++) {
            dates[i] = cob2date(date[i]);
        }
        return dates;
    }

    public static int[] date2cob(Date[] date){
        int[] dates = new int[date.length];
        for (int i = 0; i < date.length; i++) {
            dates[i] = date2cob(date[i]);
        }
        return dates;
    }

    public static Timestamp tryParse2Timestamp(String str){
        if(str == null) return null;
        return Timestamp.valueOf(str);
    }
}
