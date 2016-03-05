package com.victor.midas.train.common;

/**
 * helper for training
 */
public class MidasTrainHelper {

    /**
     * find quit signal day index, then calculate total holding period
     * buyTiming & sellTiming 0 open 1 close
     * sellTiming can be close that day means the quit signal fired based on near close info
     * otherwise sellTiming should be open next day
     */
    public static int getHoldingTime(double[] scores, int buySignalIndex, int buyTiming, int sellTiming){
        if(buySignalIndex + 1 >= scores.length) return 0;       // cannot even buy
        if(buySignalIndex + 2 >= scores.length){                // if buy at open, then sell at close
            return buyTiming == 0 ? 1 : 0;
        }
        int i = buySignalIndex + 2; //buy day is buySignalIndex + 1, so sell is at least buySignalIndex + 2
        for(;i < scores.length; i++){
            if(scores[i] < -1d){
                break;
            }
        }
        if(sellTiming == 1){    // sell at close for quit signal day
            if(i < scores.length){
                return (i - buySignalIndex - 2) * 2 + (buyTiming == 0 ? 1 : 0) + 2;
            } else {
                return (scores.length - 1 - buySignalIndex - 2) * 2 + (buyTiming == 0 ? 1 : 0);
            }
        } else {                // sell at open for next day of quit signal day
            if(i + 1 < scores.length){
                return (i - buySignalIndex - 1) * 2 + (buyTiming == 0 ? 1 : 0) + 1;
            } else {
                return (scores.length - 1 - buySignalIndex - 1) * 2 + (buyTiming == 0 ? 1 : 0);
            }
        }

    }
}
