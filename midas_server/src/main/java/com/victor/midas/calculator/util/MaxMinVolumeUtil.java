package com.victor.midas.calculator.util;

import com.victor.midas.util.MidasException;

/**
 * util to find max and min for a stock
 */
public class MaxMinVolumeUtil {

    private double[] avgVolume;
    private int len;

    private int[] maxIndex;
    private int[] minIndex;

    public MaxMinVolumeUtil(double[] avgVolume) {
        init(avgVolume);
    }

    public MaxMinVolumeUtil() {
    }

    public void init(double[] avgVolume){
        this.avgVolume = avgVolume;
        len = avgVolume.length;
    }

    public void calcMaxMinIndex(int timeFrame){
        maxIndex = new int[len];
        minIndex = new int[len];
        double maxVolume = Double.MIN_VALUE, minVolume = Double.MAX_VALUE;
        int prevMaxIndex = 0, prevMinIndex = 0;
        for (int i = 0; i < Math.min(timeFrame, len); i++) {
            // deal max price
            if(maxVolume >= avgVolume[i]){
                maxIndex[i] = prevMaxIndex;
            } else {    // update max price
                maxIndex[i] = prevMaxIndex = i;
                maxVolume = avgVolume[i];
            }
            // deal min price
            if(minVolume <= avgVolume[i]){
                minIndex[i] = prevMinIndex;
            } else {    // update min price
                minIndex[i] = prevMinIndex = i;
                minVolume = avgVolume[i];
            }
        }

        for (int i = timeFrame; i < len; i++) {
            // deal max price
            if(i - prevMaxIndex >= timeFrame){ // out of boundary
                maxVolume = Double.MIN_VALUE;
                prevMaxIndex = i - timeFrame + 1;
                for (int j = prevMaxIndex; j <= i; j++) {
                    if(maxVolume >= avgVolume[i]){
                        maxIndex[i] = prevMaxIndex;
                    } else {    // update max price
                        maxIndex[i] = prevMaxIndex = i;
                        maxVolume = avgVolume[i];
                    }
                }
            } else {
                if(maxVolume >= avgVolume[i]){
                    maxIndex[i] = prevMaxIndex;
                } else {    // update max price
                    maxIndex[i] = prevMaxIndex = i;
                    maxVolume = avgVolume[i];
                }
            }

            // deal min price
            if(i - prevMinIndex >= timeFrame){ // out of boundary
                minVolume = Double.MAX_VALUE;
                prevMinIndex = i - timeFrame + 1;
                for (int j = prevMinIndex; j <= i; j++) {
                    if(minVolume <= avgVolume[i]){
                        minIndex[i] = prevMinIndex;
                    } else {    // update min price
                        minIndex[i] = prevMinIndex = i;
                        minVolume = avgVolume[i];
                    }
                }
            } else {
                if(minVolume <= avgVolume[i]){
                    minIndex[i] = prevMinIndex;
                } else {    // update min price
                    minIndex[i] = prevMinIndex = i;
                    minVolume = avgVolume[i];
                }
            }
        }
    }

    /**
     * get max Volume before index day among timeFrame days
     */
    public double getFewDaysBeforeMaxVolume(int index){
        return avgVolume[maxIndex[index]];
    }

    /**
     * get min Volume before index day among timeFrame days
     */
    public double getFewDaysBeforeMinVolume(int index){
        return avgVolume[minIndex[index]];
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

    public int getMaxIndex(int index) {
        return maxIndex[index];
    }

    public int getMinIndex(int index) {
        return minIndex[index];
    }
}
