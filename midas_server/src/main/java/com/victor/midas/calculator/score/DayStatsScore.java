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

    private double[] upTrendPct, upTrendTime, downTrendPct, downTrendTime, correlationAgainstMarket;


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

        addIndexData("upTrendPct", upTrendPct);
        addIndexData("upTrendTime", upTrendTime);
        addIndexData("downTrendPct", downTrendPct);
        addIndexData("downTrendTime", downTrendTime);
    }

    private void calculateScore(){
        int minIdx, maxIdx;
        for (int i = 5; i < len; i++) {
            minIdx = mmPriceUtil10.getMinIndexInUpTrend(i);
            upTrendTime[i] = i - minIdx;
            upTrendPct[i] = MathStockUtil.calculateChangePct(mmPriceUtil10.getMinPrice(minIdx), end[i]);
            maxIdx = mmPriceUtil10.getMaxIndexInDownTrend(i);
            downTrendTime[i] = i - maxIdx;
            downTrendPct[i] = MathStockUtil.calculateChangePct(end[i], mmPriceUtil10.getMaxPrice(maxIdx));
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        mmPriceUtil10 = new MaxMinUtil(stock);
        mmPriceUtil10.calcMaxMinIndex(10);
        mmPriceUtil60 = new MaxMinUtil(stock);
        mmPriceUtil60.calcMaxMinIndex(60);

        upTrendPct = new double[len];
        upTrendTime = new double[len];
        downTrendPct = new double[len];
        downTrendTime = new double[len];
        correlationAgainstMarket = new double[len];
    }

}
