package com.victor.utilities.utils;

import com.victor.utilities.model.KeyValue;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * deal with date conversion
 */
public class TimeHelper {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static Date cob2date(int date) throws ParseException {
        return sdf.parse(String.valueOf(date));
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

    public static int cob2month(int cob){
        int year = cob / 10000;
        int month = (cob % 10000) / 100;
        return (year - 1900) * 12 + month;
    }

    /**
     * last business day of that month
     * @throws ParseException
     */
    public static Date month2date(int monthInt) throws ParseException {
        monthInt++;
        int year = monthInt / 12 + 1900;
        int month = monthInt % 12;
        int cob = year * 10000 + month * 100 + 1;   // next month first day
        Calendar cal = Calendar.getInstance();
        cal.setTime(cob2date(cob));

        int dayOfWeek;
        do {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        } while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
        return cal.getTime();
    }

    public static Timestamp getCurrentTimestamp(){
        return new Timestamp(new Date().getTime());
    }

    public static boolean isSameWeek(final Date d1, final Date d2) {
        return isSameWeek(new DateTime(d1), new DateTime(d2));
    }

    public static boolean isSameWeek(final DateTime d1, final DateTime d2) {
        return d1.getWeekOfWeekyear() == d2.getWeekOfWeekyear()
                && d1.getWeekyear() == d2.getWeekyear()
                && d1.getEra() == d2.getEra();
    }

    public static int daysBetween(final DateTime start, final DateTime end) {
        return Days.daysBetween(start, end).getDays();
    }

    public static int daysBetween(final Date start, final Date end) {
        return daysBetween(new DateTime(start), new DateTime(end));
    }

    public static KeyValue<Integer, Integer> count2quarter(int count) {
        int year = Math.floorDiv(count, 4) + 1970;
        int quarter = count % 4 + 1;
        return new KeyValue<>(year, quarter);
    }

    public static KeyValue<Integer, Integer> count2month(int count) {
        int year = Math.floorDiv(count, 12) + 1970;
        int month = count % 12 + 1;
        return new KeyValue<>(year, month);
    }

    public static int quarterCount(int year, int quarter) {
        return (year - 1970) * 4 + quarter - 1;
    }

    public static int monthCount(int year, int month) {
        return (year - 1970) * 12 + month - 1;
    }

    public static int quarterCountFromMonthCount(int monthCount) {
        KeyValue<Integer, Integer> ym = count2month(monthCount);
        int quarter = Math.floorDiv((ym.getValue() - 1), 3) + 1;
        return quarterCount(ym.getKey(), quarter);
    }

}
