package com.victor.midas.calculator.revert;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.divergence.IndexBadDepth;
import com.victor.midas.calculator.indicator.IndexChangePct;
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

/**
 * calculate PriceCrashRevertSignal
 */
public class PriceCrashRevertSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "pcrs";
    private MaBase maMethod = new SMA();

    private double[] score;
    private double[] dif, dea, macdBar;
    private double[] pMa5, pMa10, pMa20, pMa60;
    private MaxMinUtil mmPriceUtil5;

    public PriceCrashRevertSignal(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
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

        int sellIndex = -1;
        StockState state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20150630){
//                System.out.println("wow");
//            }

            if(state == StockState.HoldMoney) {
                if (changePct[i] < 0 && changePct[i - 1] < 0 && changePct[i - 2] > 0
                        && end[i] <= start[i] && end[i - 1] <= start[i - 1] && end[i - 2] >= start[i - 2]
                        && max[i] > min[i - 1]
                        && total[i] < total[i - 1] * 1.5
                        //&& (i + 1 < len && start[i + 1] < end[i])   // this condition need to remove in real, you cannot know tomorrow's open
                        && macdBar[i] >= 0
                        && MathStockUtil.calculateChangePct(min[i], end[i]) < 0.01
                        && howManyMaUp(i) >= 3) {
                    int maxIndex = mmPriceUtil5.getMaxIndex(i);
                    int minIndex = mmPriceUtil5.getMinIndex(maxIndex);
                    double maxPrice = mmPriceUtil5.getMaxPrice(maxIndex);
                    double minPrice = mmPriceUtil5.getMinPrice(minIndex);
                    if(MathHelper.isMoreAbs(maxPrice - end[i], maxPrice - minPrice, 0.61)){
                        score[i] = 3d;
                        state = StockState.HoldStock;
                        sellIndex = i + 2;
                    }
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

    private int howManyMaUp(int i){
        int cnt = 0;
        if(pMa5[i] > pMa5[i - 1]) cnt++;
        if(pMa10[i] > pMa10[i - 1]) cnt++;
        if(pMa20[i] > pMa20[i - 1]) cnt++;
        if(pMa60[i] > pMa60[i - 1]) cnt++;
        return cnt;
    }

    @Override
    protected void initIndex() throws MidasException {
        dif = (double[])stock.queryCmpIndex("dif");
        dea = (double[])stock.queryCmpIndex("dea");
        macdBar = (double[])stock.queryCmpIndex("macdBar");
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
