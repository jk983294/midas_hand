package com.victor.midas.train.common;

import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;

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

    /**
     * find quit signal day index, then calculate total holding period
     * buyTiming 0 buy signal day open, 1 buy signal day close
     * sellTiming 0 next quit signal day open, 1 quit signal day close
     * sellTiming can be close that day means the quit signal fired based on near close info
     * otherwise sellTiming should be open next day
     */
    public static void getHoldingTime(double[] scores, StockScore score, int buySignalIndex, StockVo stock){
        if(buySignalIndex + 1 >= scores.length) return;         // cannot even buy
        score.buyIndex = buySignalIndex + 1;
        score.buyCob = stock.getCobByIndex(buySignalIndex + 1);
        if(buySignalIndex + 2 >= scores.length){                // if buy at open, then sell at close
            score.sellIndex = score.buyIndex;
            score.sellCob = score.buyCob;
            score.sellTiming = 1;
            return;
        }
        int i = buySignalIndex + 1; //buy day is buySignalIndex + 1, so sell is at least buySignalIndex + 1
        for(;i < scores.length; i++){
            if(scores[i] < -1d){
                break;
            }
        }
        if(score.sellTiming == 1){  // sell at close for quit signal day
            if(i == buySignalIndex + 1){    // sell signal is fired at
                if(i + 1 < scores.length){
                    score.sellIndex = i + 1;
                    score.sellCob = stock.getCobByIndex(i + 1);
                    // even the option says it will sell at close, but today close is not available, then sell at open next
                    score.sellTiming = 0;
                } else {
                    score.sellIndex = score.buyIndex;
                    score.sellCob = score.buyCob;
                }
            } else if(i < scores.length){
                score.sellIndex = i;
                score.sellCob = stock.getCobByIndex(i);
            } else {
                score.sellIndex = scores.length - 1;
                score.sellCob = stock.getCobByIndex(scores.length - 1);
            }
        } else {                    // sell at open for next day of quit signal day
            if(i + 1 < scores.length){
                score.sellIndex = i + 1;
                score.sellCob = stock.getCobByIndex(i + 1);
            } else {
                score.sellIndex = scores.length - 1;
                score.sellCob = stock.getCobByIndex(scores.length - 1);
                // even the option says it will sell at next open, but next open is not available, then sell at close
                score.sellTiming = 1;
            }
        }
        score.holdingPeriod = score.sellIndex - score.buyIndex + 1;
    }
}
