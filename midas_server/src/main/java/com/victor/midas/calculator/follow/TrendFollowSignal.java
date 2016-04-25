package com.victor.midas.calculator.follow;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.divergence.IndexBadDepth;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.macd.IndexMACD;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import com.victor.utilities.utils.MathHelper;

import java.util.ArrayList;

/**
 * calculate TrendFollowSignal
 */
public class TrendFollowSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "score_tfs";
    private MaBase maMethod = new SMA();

    private double[] score;
    private double[] dif, dea, macdBar;
    private double[] pMa5, pMa10, pMa20, pMa60, vMa5;
    private double[] middleShadowPct, upShadowPct, downShadowPct;
    private int[] maBullCnt;
    private MaxMinUtil mmPriceUtil5;
    private int sellIndex;
    private StockState state;

    public TrendFollowSignal(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
        requiredCalculators.add(IndexBadDepth.INDEX_NAME);
        requiredCalculators.add(IndexMACD.INDEX_NAME);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        pMa5 = maMethod.calculate(end, 5);
        pMa10 = maMethod.calculate(end, 10);
        pMa20 = maMethod.calculate(end, 20);
        pMa60 = maMethod.calculate(end, 60);
        vMa5 = maMethod.calculate(total, 5);
        maBullCnt = new int[len];
        ArrayList<Integer> points = new ArrayList<>();
        boolean isLastPointBullForm = false;

        sellIndex = -1;
        state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20150703){
//                System.out.println("wow");
//            }
            maBullCnt[i] = howManyMaBullForm(i);
            if(maBullCnt[i] == 6 && maBullCnt[i - 1] < 6){
                points.add(i);
                isLastPointBullForm = true;
            } else if(maBullCnt[i - 1] == 6 && maBullCnt[i] < 6){
                points.add(i);
                isLastPointBullForm = false;
            }

            if(state == StockState.HoldMoney) {
                if (isLastPointBullForm && macdBar[i] >= 0 && i - points.get(points.size() - 1) < 6
                        && (changePct[i] < 0d || (changePct[i - 1] < 0d && changePct[i] < 0.01d))
                        && changePct[i] > -0.098
                        && MathHelper.isLessAbs(macdBar[i - 1], macdBar[i], singleDouble)
//                        && total[i] < total[i - 1] * 1.5
//                        && MathStockUtil.calculateChangePct(min[i], end[i]) < 0.01
                        ) {
//                    int maxIndex = mmPriceUtil5.getMaxIndex(i);
//                    int minIndex = mmPriceUtil5.getMinIndex(maxIndex);
//                    double maxPrice = mmPriceUtil5.getMaxPrice(maxIndex);
//                    double minPrice = mmPriceUtil5.getMinPrice(minIndex);
//                    if(MathHelper.isMoreAbs(maxPrice - end[i], maxPrice - minPrice, 0.61)){
//
//                    }
                    setBuy(4.6d, i);
                }

            } else if(state == StockState.HoldStock && i == sellIndex){
                score[i] = -5d;
                state = StockState.HoldMoney;
                sellIndex = -1;
            }
//            score[i] = lastSection.status1.ordinal();
        }
        addIndexData(INDEX_NAME, score);
    }

    private void setBuy(double currentScore, int idx){
        if(currentScore > score[idx]){
            score[idx] = currentScore;
            state = StockState.HoldStock;
            sellIndex = idx + 2;
        }
    }

    private int howManyMaBullForm(int i){
        int cnt = 0;
        if(pMa60[i] <= pMa20[i]) cnt++;
        if(pMa60[i] <= pMa10[i]) cnt++;
        if(pMa60[i] <= pMa5[i]) cnt++;
        if(pMa20[i] <= pMa10[i]) cnt++;
        if(pMa20[i] <= pMa5[i]) cnt++;
        if(pMa10[i] <= pMa5[i]) cnt++;
        return cnt;
    }

    @Override
    protected void initIndex() throws MidasException {
        dif = (double[])stock.queryCmpIndex("dif");
        dea = (double[])stock.queryCmpIndex("dea");
        macdBar = (double[])stock.queryCmpIndex("macdBar");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        score = new double[len];
        mmPriceUtil5 = new MaxMinUtil(stock, false);
        mmPriceUtil5.calcMaxMinIndex(5);
    }

    @Override
    public MidasTrainOptions getTrainOptions() {
        MidasTrainOptions options = new MidasTrainOptions();
        options.selectTops = false;
        options.useSignal = true;
        return options;
    }
}
