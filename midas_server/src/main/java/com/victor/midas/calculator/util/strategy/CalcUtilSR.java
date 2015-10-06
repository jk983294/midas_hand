package com.victor.midas.calculator.util.strategy;

import com.victor.midas.calculator.common.CalcUtil;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.MathHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * calculate delta
 */
public class CalcUtilSR extends CalcUtil {

    private double trendHigh, trendLow;

    public CalcUtilSR(){
    }

    public CalcUtilSR(StockVo stock) throws MidasException {
        init(stock);
    }

    public void setBoundaryForBuy(int index){
        int tieIndex1 , tieIndex2;
        tieIndex1 = firstTieNodeIndex(index);
        tieIndex2 = ctf0[tieIndex1];
        trendHigh = end[tieIndex2];
        trendLow = end[tieIndex1];
    }

    public boolean isSellTimeWithinBoundary(int index){
        //if(changePct[index] < 0 && end[index] < trendHigh) return true;
        if(changePct[index] > 0 && MathHelper.isInRange(MathStockUtil.calculateChangePct(trendHigh, end[index]), -0.01, 0)) return true;
        return false;
    }

    public boolean isBreakMaLong(int index){
        double diffPct = MathStockUtil.calculateChangePct(pMaLong[index], end[index]);
        return diffPct < 0.0 || (changePct[index] < -0.02 && diffPct < 0.01);
    }

    /**
     * for good period, must current point is down trend
     **/
    public boolean isTrendOK(int index){
        int tieIndex1 = index, tieIndex2 = 0, tieIndex3 = 0, findCnt = 1;
        ArrayList<Integer> tps = new ArrayList<>();
        ArrayList<Double> trendPct = new ArrayList<>();
        ArrayList<Double> avgVol = new ArrayList<>();
        tps.add(index);
        tieIndex1 = firstTieNodeIndex(index);
        if(tieIndex1 >= 0){
            tieIndex2 = ctf0[tieIndex1];
            do {
                tps.add(tieIndex1);
                tieIndex1 = tieIndex2;
                tieIndex2 = ctf0[tieIndex1];
            } while (index - tieIndex1 < 15 && tieIndex1 != tieIndex2);
        }
        if(middleShadowPct[index] < 0){
            Collections.reverse(tps);       // nature order
            findCnt = tps.size();
            for (int i = findCnt - 1; i > 0; --i) {
                trendPct.add(accumulateChangePct(tps.get(i - 1) + 1, tps.get(i)));
                avgVol.add(avgVolume(tps.get(i - 1) + 1, tps.get(i)));
            }
            if(findCnt >= 3 && trendPreVolRatio[index] < 1 && !hasKStateSell(tps.get(findCnt - 2) + 1, tps.get(findCnt-1))){
                if(findCnt == 3){
                    if(Math.abs(trendPct.get(0) / trendPct.get(1)) < 0.9  ) return true;
                } else if(findCnt >= 4){
                    if(end[tps.get(3)] > end[tps.get(0)] ) return true;
                }
            }
        }
        return false;
    }

    public boolean isTrendUpWeak(int index){
        return (pMa20D1[index] > 0) && (pMa60D1[index] > 0);
    }

    public boolean isVolumePriceRatioAnormal(int index){
        if(changePct[index] > 0.05
                && total[index - 1] * Math.abs(middleShadowPct[index]) > total[index] * Math.abs(middleShadowPct[index - 1]) ){
            return true;
        }
        return false;
    }

    public boolean isVolumeDownEnough(int index){
        return volRatio[index] < 0.9;
    }

    public boolean isVolumeUpEnough(int index){
        return volRatio[index] > 1d;
    }
}
