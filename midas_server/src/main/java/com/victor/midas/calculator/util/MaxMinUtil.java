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

    private int[] maxIndex;
    private int[] minIndex;

    public MaxMinUtil(StockVo stock) throws MidasException {
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
        int prevMaxIndex = 0, prevMinIndex = 0;
        for (int i = 0; i < Math.min(timeFrame, len); i++) {
            // deal max price
            if(maxPrice >= getMaxPrice(i)){
                maxIndex[i] = prevMaxIndex;
            } else {    // update max price
                maxIndex[i] = prevMaxIndex = i;
                maxPrice = getMaxPrice(i);
            }
            // deal min price
            if(minPrice <= getMinPrice(i)){
                minIndex[i] = prevMinIndex;
            } else {    // update min price
                minIndex[i] = prevMinIndex = i;
                minPrice = getMinPrice(i);
            }
        }

        for (int i = timeFrame; i < len; i++) {
            // deal max price
            if(i - prevMaxIndex >= timeFrame){ // out of boundary
                maxPrice = Double.MIN_VALUE;
                prevMaxIndex = i - timeFrame + 1;
                for (int j = prevMaxIndex; j <= i; j++) {
                    if(maxPrice >= getMaxPrice(i)){
                        maxIndex[i] = prevMaxIndex;
                    } else {    // update max price
                        maxIndex[i] = prevMaxIndex = i;
                        maxPrice = getMaxPrice(i);
                    }
                }
            } else {
                if(maxPrice >= getMaxPrice(i)){
                    maxIndex[i] = prevMaxIndex;
                } else {    // update max price
                    maxIndex[i] = prevMaxIndex = i;
                    maxPrice = getMaxPrice(i);
                }
            }

            // deal min price
            if(i - prevMinIndex >= timeFrame){ // out of boundary
                minPrice = Double.MAX_VALUE;
                prevMinIndex = i - timeFrame + 1;
                for (int j = prevMinIndex; j <= i; j++) {
                    if(minPrice <= getMinPrice(i)){
                        minIndex[i] = prevMinIndex;
                    } else {    // update min price
                        minIndex[i] = prevMinIndex = i;
                        minPrice = getMinPrice(i);
                    }
                }
            } else {
                if(minPrice <= getMinPrice(i)){
                    minIndex[i] = prevMinIndex;
                } else {    // update min price
                    minIndex[i] = prevMinIndex = i;
                    minPrice = getMinPrice(i);
                }
            }
        }
    }

    /**
     * get max price before index day among timeFrame days
     */
    public double getFewDaysBeforeMaxPrice(int index){
        return getMaxPrice(getMaxIndex(index));
    }

    /**
     * get min price before index day among timeFrame days
     */
    public double getFewDaysBeforeMinPrice(int index){
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


    public double getMaxPrice(int index){
        return Math.max(start[index], end[index]);
    }

    public double getMinPrice(int index){
        return Math.min(start[index], end[index]);
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
