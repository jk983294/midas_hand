package com.victor.midas.calculator.divergence;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.IndexMACD;
import com.victor.midas.calculator.indicator.IndexPriceMA;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;

/**
 * this is used for get big picture when should let go cause it is too risky
 */
public class IndexBadDepth extends IndexCalcBase {

    public final static String INDEX_NAME = "badDepth";

    private double[] badDepth;

    private double[] end, start, max, min, total, changePct, middleShadowPct;
    private double[] vMa5;
    private double[] dif, dea, macdBar; // white line, yellow line, bar

    private MaxMinUtil mmPriceUtil60, mmPriceUtil5;

    private int len;

    public IndexBadDepth(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
        //requiredCalculators.add(IndexMACD.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        calculateIndex();
        addIndexData("badDepth", badDepth);
    }

    private void calculateIndex(){
        int minIndex, maxIndex, cob, cobIndex;
        int[] dates = filterUtil.getIndexSH().getDatesInt();
        double changePctFromMa, changePctFromHigh, volumeChange;
        if(stock.getStockType() == StockType.Index){
            MaBase maMethod = new SMA();
            double[] ma = maMethod.calculate(end, 5);
            double[] vMa = maMethod.calculate(total, 5);
            for( int i = 5; i < len; i++) {
                changePctFromMa = MathStockUtil.calculateChangePct(end[i], ma[i]);
                volumeChange = MathStockUtil.calculateChangePct(vMa[i - 5], total[i]);
                if(end[i] < ma[i] && changePctFromMa < 0.0091 && Math.abs(middleShadowPct[i]) < 0.0051){
                    badDepth[i] = -5d;
                }
            }
        } else {
//            for( int i = 5; i < len; i++) {
//                minIndex = mmPriceUtil5.getMinIndexRecursive(i);
//                maxIndex = mmPriceUtil5.getMaxIndexRecursive(minIndex);
//                changePctFromHigh = MathStockUtil.calculateChangePct(max[i], max[maxIndex]);
//                volumeChange = vMa5[i] / vMa5[maxIndex];
//                if(i - maxIndex > 10 && volumeChange < 0.8 && changePctFromHigh > 0d && changePctFromHigh < 0.03){
//                    badDepth[i] += -5d;
//                }
//            }
            stock.setCobIndex(0);
            boolean isNotSameDay = false;
            int stopCnt = 0;
//            for (int i = 0; i < dates.length; i++) {
//                cob = dates[i];
//                cobIndex = stock.getCobIndex();
//                if(!stock.isSameDayWithIndex(cob)){
//                    if(isNotSameDay) stopCnt++;
//                    else {
//                        isNotSameDay = true;
//                        stopCnt = 1;
//                    }
//                } else {
//                    if(isNotSameDay){
//                        for( int j = Math.max(cobIndex - 1, 0); j < cobIndex + singleInt && j < len; j++) {
//                            badDepth[j] = -5d;
//                        }
//                    }
//                    isNotSameDay = false;
//                    stopCnt = 0;
//                }
//                stock.advanceIndex(cob);
//            }
        }

    }

    @Override
    protected void initIndex() throws MidasException {
        MaBase maMethod = new SMA();
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
//        dif = (double[])stock.queryCmpIndex("dif");
//        dea = (double[])stock.queryCmpIndex("dea");
//        macdBar = (double[])stock.queryCmpIndex("macdBar");
        mmPriceUtil60 = new MaxMinUtil(stock, false);
        mmPriceUtil60.calcMaxMinIndex(60);
        mmPriceUtil5 = new MaxMinUtil(stock, false);
        mmPriceUtil5.calcMaxMinIndex( 15);
        vMa5 = maMethod.calculate(total, 5);
        len = end.length;

        badDepth = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }
}
