package com.victor.midas.calculator.util;

import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.stat.StatUtils;

/**
 * util to find max and min for a stock
 */
public class MaxMinUtil {

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;
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
        len = end.length;
    }

    public void calcMaxMinIndex(int timeFrame){
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
        return useEndStartPair ? Math.min(start[index], end[index]) : Math.max(max[index], min[index]);
    }

    public int getMaxIndex(int index) {
        return maxIndex[index];
    }

    public int getMinIndex(int index) {
        return minIndex[index];
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
}
