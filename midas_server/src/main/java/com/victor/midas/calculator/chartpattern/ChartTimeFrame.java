package com.victor.midas.calculator.chartpattern;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.FibonacciSequence;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;

import java.util.*;

/**
 * based on Financial Time Series Segmentation Based On Turning Points, Jiangling Yin, Yain-Whar Si, Zhiguo Gong
 */
public class ChartTimeFrame extends IndexCalcBase {

    public static final String INDEX_NAME = "ctf";

    private static final int UP = -1;
    private static final int TIE = 2;
    private static final int DOWN = -2;
    private static final int TIE_TIME_FRAME_THRESHOLD = 3;
    private static final double TIE_SWING_THRESHOLD = 0.02;
    private static final double IS_NEAR_THRESHOLD_BASE = 0.01;
    private static final int TOTAL_LEVEL = 1;
    private static double IS_NEAR_CHANGE_PCT_THRESHOLD[] = null;
    private static int IS_NEAR_DAY_THRESHOLD[] = null;
    static {
        IS_NEAR_CHANGE_PCT_THRESHOLD = new double[TOTAL_LEVEL];
        IS_NEAR_DAY_THRESHOLD = new int[TOTAL_LEVEL];
        for (int i = 0; i < TOTAL_LEVEL; i++) {
            IS_NEAR_CHANGE_PCT_THRESHOLD[i] = IS_NEAR_THRESHOLD_BASE + 0.001 * i;
            IS_NEAR_DAY_THRESHOLD[i] = FibonacciSequence.queryFibonacciNumber(i+5);
        }
    }

    public ChartTimeFrame(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        int[] ctf = new int[len];

        ArrayList<Integer> ips = calculateInitialTP(end, changePct, len);
        buildTrendArray(ctf, ips, end);
        addIndexData("ctf0", ctf);

        for (itr = 0; itr < TOTAL_LEVEL; itr++) {
            ips = turningPoints(ips, end, itr);
            ips =  removeSmallTrendTurningPoints(ips, end, itr);
            ctf = new int[len];
            buildTrendArray(ctf, ips, end);
            addIndexData("ctf"+(itr+1), ctf);
        }
    }

    @Override
    protected void initIndex() throws MidasException {
    }

    /**
     * calculate turning point, maximum and minimum point of certain period
     */
    private ArrayList<Integer> calculateInitialTP(double[] end, double[] changePct, int len){
        ArrayList<Integer> list = new ArrayList<>();
        /*** initial run to get local minimum or maximum point */
        list.add(0);
        for (int i = 1; i < len; i++) {
            if( i - 1 < 0) list.add(i);             //first day
            else if( i + 1 >= len) list.add(i);     //last day
            // yesterday and tomorrow are not same trend, then today should be consider tie
            else if( ! MathHelper.isSameSign(changePct[i], changePct[i+1])  ){
                list.add(i);
            }
        }
        // add last day as IP
        if(list.get(list.size() - 1) != len - 1){
            list.add(len - 1);
        }
        return list;
    }

    /**
     * one iterate for calculating higher level turning points list
     */
    private ArrayList<Integer> turningPoints(ArrayList<Integer> lowLevel, double[] end, int level){
        ArrayList<Integer> highLevel = new ArrayList<>();
        highLevel.add(lowLevel.get(0));
        int d1, d2, d3, d4, len = lowLevel.size(), i;
        for (i = 1; i < len - 3; ++i) {
            d1 = highLevel.get(highLevel.size() - 1);   // get last high level
            d2 = lowLevel.get(i);
            d3 = lowLevel.get(i+1);
            d4 = lowLevel.get(i+2);

            if(d2 - d1 > IS_NEAR_DAY_THRESHOLD[level]){
                highLevel.add(d2);
                continue;
            }

            //up trend
            if(end[d1] < end[d3] && end[d3] < end[d2] && end[d2] < end[d4]  &&
                    Math.abs(end[d2] - end[d3]) < Math.abs(end[d1] - end[d3]) + Math.abs(end[d2] - end[d4])){
                i += 1;
                continue;
            }
            //down trend
            else if(end[d1] > end[d2] && end[d1] > end[d3] && end[d2] > end[d4] && end[d3] > end[d4] &&
                    Math.abs(end[d2] - end[d3]) < Math.abs(end[d1] - end[d3]) + Math.abs(end[d2] - end[d4])){
                i += 1;
                continue;
            }
            // near trend
            else if(isNear(end[d1], end[d3], level) && isNear(end[d2], end[d4], level)){
                i += 1;
                continue;
            }
            highLevel.add(d2);
        }
        // add last un-used points
        for (; i < len ; ++i) {
            highLevel.add(lowLevel.get(i));
        }
        return highLevel;
    }

    /**
     * one iterate for calculating higher level turning points list
     */
    private ArrayList<Integer> removeSmallTrendTurningPoints(ArrayList<Integer> lowLevel, double[] end, int level){
        ArrayList<Integer> highLevel = new ArrayList<>();
        highLevel.add(lowLevel.get(0));
        int d1, d2, d3, d4, len = lowLevel.size(), i;
        int closeEnoughDay = FibonacciSequence.queryFibonacciNumber(level + 2);
        double trend1, trend2;
        for (i = 1; i <= len - 3; ++i) {
            d1 = highLevel.get(highLevel.size() - 1);   // get last high level
            d2 = lowLevel.get(i);
            d3 = lowLevel.get(i+1);
            d4 = lowLevel.get(i+2);
            // near tie node, but separate two same trends
            if(d2 - d1 <= closeEnoughDay){
                trend1 = MathStockUtil.calculateChangePct(end[d1],end[d2]);
                trend2 = MathStockUtil.calculateChangePct(end[d2],end[d3]);
                if(MathHelper.isSameSignStrong(trend1, trend2)) {
                    i += 1;
                    continue;
                }
            }
            if(d3 - d2 <= closeEnoughDay){
                trend1 = MathStockUtil.calculateChangePct(end[d1],end[d2]);
                trend2 = MathStockUtil.calculateChangePct(end[d3],end[d4]);
                if(MathHelper.isSameSignStrong(trend1, trend2)) {
                    i += 1;
                    continue;
                }
            }
            highLevel.add(d2);
        }
        // check last -1 point
        for (; i <= len - 2; ++i) {
            d1 = highLevel.get(highLevel.size() - 1);   // get last high level
            d2 = lowLevel.get(i);
            d3 = lowLevel.get(i+1);
            // near tie node, but separate two same trends
            if(d2 - d1 <= closeEnoughDay){
                trend1 = MathStockUtil.calculateChangePct(end[d1],end[d2]);
                trend2 = MathStockUtil.calculateChangePct(end[d2],end[d3]);
                if(MathHelper.isSameSignStrong(trend1, trend2)) {
                    i += 1;
                    continue;
                }
            }
        }
        // add last un-used points
        for (; i < len ; ++i) {
            highLevel.add(lowLevel.get(i));
        }
        return highLevel;
    }

    /**
     * measure two end price is close enough
     */
    private boolean isNear(double x, double y, int level){
        if( x == 0) return Math.abs(y) < IS_NEAR_CHANGE_PCT_THRESHOLD[level];
        else return Math.abs((x-y)/x) < IS_NEAR_CHANGE_PCT_THRESHOLD[level];
    }

    /**
     * build trend array from important points
     */
    private void buildTrendArray(int[] trend, ArrayList<Integer> tps, double[] end){
        int previousDayIndex, dayIndex;
        int length = tps.size();
        for (int i = 1; i < length; ++i) {
            previousDayIndex = tps.get(i-1);
            dayIndex = tps.get(i);

            if(end[previousDayIndex] < end[dayIndex]){
                ArrayHelper.fill(trend, previousDayIndex + 1, dayIndex, UP);
            } else {
                ArrayHelper.fill(trend, previousDayIndex + 1, dayIndex, DOWN);
            }
        }

        for (int i = 1; i < length; ++i) {
            trend[tps.get(i)] = tps.get(i - 1);
        }

        /*** set first tps point to itself */
        if(length > 2){
            trend[tps.get(0)] = 0;
//            // set first day
//            previousDayIndex = tps.get(0);
//            dayIndex = tps.get(1);
//            if(end[previousDayIndex] > end[dayIndex]) trend[previousDayIndex] = DOWN;
//            else trend[previousDayIndex] = UP;
//
//            //set last day
//            previousDayIndex = tps.get(weight - 2);
//            dayIndex = tps.get(weight - 1);
//            if(end[previousDayIndex] > end[dayIndex]) trend[dayIndex] = DOWN;
//            else trend[dayIndex] = UP;
        }
    }
}
