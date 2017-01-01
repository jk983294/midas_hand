package com.victor.midas.calculator.util;

import com.victor.midas.calculator.score.week.WeeklyStockData;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.util.List;

/**
 * util to find max and min for a stock
 */
public class MaxMinUtil {

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;
    private int[] dates;
    private int len;

    private boolean useEndStartPair = true;

    private int[] maxIndex;
    private int[] minIndex;

    public MaxMinUtil(StockVo stock) throws MidasException {
        init(stock);
    }

    public MaxMinUtil(StockVo stock, boolean useEndStartPair) throws MidasException {
        this.useEndStartPair = useEndStartPair;
        init(stock);
    }

    public MaxMinUtil() {
    }

    public void init(StockVo stock) throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        dates = stock.getDatesInt();
        len = end.length;
    }

    public void init(List<WeeklyStockData> weeks) throws MidasException {
        if(CollectionUtils.isNotEmpty(weeks)){
            len = weeks.size();
            end = new double[len];
            start = new double[len];
            max = new double[len];
            min = new double[len];
            dates = new int[len];
            for (int i = 0; i < len; i++) {
                end[i] = weeks.get(i).end;
                start[i] = weeks.get(i).start;
                max[i] = weeks.get(i).max;
                min[i] = weeks.get(i).min;
                dates[i] = weeks.get(i).cobTo;
            }
        }
    }

    public void calcMaxMinIndex(int timeFrame){
        if(len <= 0) return;
        maxIndex = new int[len];
        minIndex = new int[len];
        double maxPrice = Double.MIN_VALUE, minPrice = Double.MAX_VALUE;
        if(len > 0){
            maxIndex[0] = minIndex[0] = 0;
            maxPrice = getMaxPrice(0);
            minPrice = getMinPrice(0);
        }
        for (int i = 1; i < Math.min(timeFrame, len); i++) {
            // deal max price
            if(maxPrice >= getMaxPrice(i)){
                maxIndex[i] = maxIndex[i - 1];
            } else {    // update max price
                maxIndex[i] = i;
                maxPrice = getMaxPrice(i);
            }
            // deal min price
            if(minPrice <= getMinPrice(i)){
                minIndex[i] = minIndex[i - 1];
            } else {    // update min price
                minIndex[i] = i;
                minPrice = getMinPrice(i);
            }
        }

        for (int i = timeFrame; i < len; i++) {
            // deal max price
            if(i - maxIndex[i - 1] >= timeFrame){ // out of boundary
                maxIndex[i] = i - timeFrame + 1;
                maxPrice = getMaxPrice(i - timeFrame + 1);
                for (int j = i - timeFrame + 2; j <= i; j++) {
                    if(maxPrice < getMaxPrice(j)){
                        maxIndex[i] = j;
                        maxPrice = getMaxPrice(j);
                    }
                }
            } else {
                if(maxPrice < getMaxPrice(i)){
                    maxIndex[i] = i;
                    maxPrice = getMaxPrice(i);
                } else {
                    maxIndex[i] = maxIndex[i - 1];
                }
            }

            // deal min price
            if(i - minIndex[i - 1] >= timeFrame){ // out of boundary
                minIndex[i] = i - timeFrame + 1;
                minPrice = getMinPrice(i - timeFrame + 1);
                for (int j = i - timeFrame + 2; j <= i; j++) {
                    if(minPrice > getMinPrice(j)){
                        minIndex[i] = j;
                        minPrice = getMinPrice(j);
                    }
                }
            } else {
                if(minPrice > getMinPrice(i)){
                    minIndex[i] = i;
                    minPrice = getMinPrice(i);
                } else {
                    minIndex[i] = minIndex[i - 1];
                }
            }
        }
    }

    /**
     * get max price before index day (include index day) among timeFrame days
     */
    public double getMaxPriceAmongTimeFrame(int index){
        return getMaxPrice(getMaxIndex(index));
    }

    /**
     * get min price before index day (include index day) among timeFrame days
     */
    public double getMinPriceAmongTimeFrame(int index){
        return getMinPrice(getMinIndex(index));
    }

    /**
     * get how many days passed when last max occur
     */
    public int getMaxIndexPeriod(int index) {
        return index - maxIndex[index];
    }

    /**
     * get how many days passed when last min occur
     */
    public int getMinIndexPeriod(int index) {
        return index - minIndex[index];
    }


    /**
     * could use max min pair instead of start / end pair
     */
    public double getMaxPrice(int index){
        return useEndStartPair ? Math.max(start[index], end[index]) : Math.max(max[index], min[index]);
    }

    /**
     * could use max min pair instead of start / end pair
     */
    public double getMinPrice(int index){
        return useEndStartPair ? Math.min(start[index], end[index]) : Math.min(max[index], min[index]);
    }

    public int getMaxIndex(int index) {
        return maxIndex[index];
    }

    public int getMinIndex(int index) {
        return minIndex[index];
    }

    public int getMaxIndexRecursive(int index) {
        return getMaxIndexRecursive(index, false);
    }

    public int getMinIndexRecursive(int index) {
        return getMinIndexRecursive(index, false);
    }

    public int getMaxIndexRecursive(int index, boolean isIgnoreSameDayHighLow) {
        int current = index;
        do {
            current = maxIndex[current];
            if(isIgnoreSameDayHighLow && maxIndex[current] == minIndex[current]){
                current--;
            }
        } while (current >= 0 && current != maxIndex[current]);
        return Math.max(0, current);
    }

    public int getMinIndexRecursive(int index, boolean isIgnoreSameDayHighLow) {
        int current = index;
        do {
            current = minIndex[current];
            if(isIgnoreSameDayHighLow && maxIndex[current] == minIndex[current]){
                current--;
            }
        } while (current >= 0 && current != minIndex[current]);
        return Math.max(0, current);
    }

    public int getMaxIndexInDownTrend(int index) {
        int current = getMaxIndexRecursive(index);
        int maxIdx;
        do {
            maxIdx = current;
            current = getMaxIndexRecursive(getMinIndexRecursive(maxIdx));
        } while (current >= 0 && current != maxIdx && getMaxPrice(current) > getMaxPrice(maxIdx));
        return getMaxPrice(maxIdx) > getMaxPrice(current) ? maxIdx : current;
    }

    public int getMinIndexInUpTrend(int index) {
        int current = getMinIndexRecursive(index);
        int minIdx;
        do {
            minIdx = current;
            current = getMinIndexRecursive(getMaxIndexRecursive(minIdx));
        } while (current >= 0 && current != minIdx && getMinPrice(current) < getMinPrice(minIdx));
        return getMinPrice(minIdx) < getMinPrice(current) ? minIdx : current;
    }

    public int[] getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(int[] maxIndex) {
        this.maxIndex = maxIndex;
    }

    public int[] getMinIndex() {
        return minIndex;
    }

    public void setMinIndex(int[] minIndex) {
        this.minIndex = minIndex;
    }

    public void setUseEndStartPair(boolean useEndStartPair) {
        this.useEndStartPair = useEndStartPair;
    }
}
