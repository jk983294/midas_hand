package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasException;


public class DayStatsScore extends IndexCalcBase {

    public static final String INDEX_NAME = "dss";

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    private MaxMinUtil mmPriceUtil10, mmPriceUtil60;

    private double[] longTermUpPct, shortTermDownPct,
            longTermDownPct, shortTermUpPct, upSlow, downFast, correlationAgainstMarket;


    public DayStatsScore(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        calculateScore();

        addIndexData("longTermUpPct", longTermUpPct);
        addIndexData("longTermDownPct", longTermDownPct);
        addIndexData("shortTermUpPct", shortTermUpPct);
        addIndexData("shortTermDownPct", shortTermDownPct);

        addIndexData("upSlow", upSlow);
        addIndexData("downFast", downFast);
    }

    private void calculateScore(){
        int minIdx, maxIdx;
        for (itr = 5; itr < len; itr++) {
//            if(dates[i] == 20160914){
//                System.out.println("wow");
//            }
            longTermUpPct[itr] = MathStockUtil.calculateChangePct(
                    mmPriceUtil60.getMinPrice(mmPriceUtil60.getMinIndexInUpTrend(itr)), end[itr]);
            longTermDownPct[itr] = MathStockUtil.calculateChangePct(
                    end[itr], mmPriceUtil60.getMaxPrice(mmPriceUtil60.getMaxIndexInDownTrend(itr)));
            shortTermUpPct[itr] = MathStockUtil.calculateChangePct(
                    mmPriceUtil60.getMinPrice(mmPriceUtil10.getMinIndexInUpTrend(itr)), end[itr]);
            shortTermDownPct[itr] = MathStockUtil.calculateChangePct(
                    end[itr], mmPriceUtil60.getMaxPrice(mmPriceUtil10.getMaxIndexInDownTrend(itr)));

            /**
             * bottom fishing, if stock suffer a long term down trend, and go up very slow
             */
            upSlow[itr] = longTermDownPct[itr] * ((double)(itr - mmPriceUtil10.getMinIndexInUpTrend(itr))) / (0.01 + shortTermUpPct[itr]);

            /**
             * bottom fishing, if stock suffer a long term down trend, and then accelerate to drop
             */
            if(itr - mmPriceUtil10.getMaxIndexInDownTrend(itr) > 4){
                downFast[itr] = 1000d * longTermDownPct[itr] * shortTermDownPct[itr] / ((double)(itr - mmPriceUtil10.getMaxIndexInDownTrend(itr) + 1));
            } else {
                downFast[itr] = 0d;
            }

        }
    }

    @Override
    protected void initIndex() throws MidasException {
        mmPriceUtil10 = new MaxMinUtil(stock);
        mmPriceUtil10.calcMaxMinIndex(10);
        mmPriceUtil60 = new MaxMinUtil(stock);
        mmPriceUtil60.calcMaxMinIndex(60);

        longTermUpPct = new double[len];
        shortTermDownPct = new double[len];
        longTermDownPct = new double[len];
        shortTermUpPct = new double[len];
        upSlow = new double[len];
        downFast = new double[len];
        correlationAgainstMarket = new double[len];
    }

}
