package com.victor.midas.calculator.revert;

import com.victor.midas.calculator.common.IndexCalcBase;
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

/**
 * calculate PriceCrashRevertSignal
 */
public class PriceCrashRevertSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "pcrs";
    private MaBase maMethod = new SMA();

    private double[] score;
    private double[] dif, dea, macdBar;
    private double[] pMa5, pMa10, pMa20, pMa60, vMa5;
    private double[] middleShadowPct, upShadowPct, downShadowPct;
    private MaxMinUtil mmPriceUtil5;
    private int sellIndex;

    public PriceCrashRevertSignal(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
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

        sellIndex = -1;
        state = StockState.HoldMoney;
        for (itr = 5; itr < len; itr++) {
//            if(dates[i] == 20160411){
//                System.out.println("wow");
//            }

            if(state == StockState.HoldMoney) {
                if (changePct[itr] < 0 && changePct[itr - 1] < 0 && changePct[itr - 2] > 0
                        && end[itr] <= start[itr] && end[itr - 1] <= start[itr - 1] && end[itr - 2] >= start[itr - 2]
                        && max[itr] > min[itr - 1]
                        && total[itr] < total[itr - 1] * 1.5
                        && macdBar[itr] >= 0
                        && MathStockUtil.calculateChangePct(min[itr], end[itr]) < 0.01
                        && howManyMaUp(itr) >= 3) {
                    int maxIndex = mmPriceUtil5.getMaxIndex(itr);
                    int minIndex = mmPriceUtil5.getMinIndex(maxIndex);
                    double maxPrice = mmPriceUtil5.getMaxPrice(maxIndex);
                    double minPrice = mmPriceUtil5.getMinPrice(minIndex);
                    if(MathHelper.isMoreAbs(maxPrice - end[itr], maxPrice - minPrice, 0.61)){
                        setBuy(4.6d, itr);
                    }
                }
                if(changePct[itr] < 0 && macdBar[itr] >= 0 && isMaBullForm(itr - 1)){
                    if((MathHelper.isLessAbs(MathStockUtil.calculateChangePct(pMa20[itr], min[itr]), 0.01)
                            || MathHelper.isLessAbs(MathStockUtil.calculateChangePct(pMa20[itr], end[itr]), 0.01))
                            && changePct[itr] > -0.097d
                            && Math.abs(middleShadowPct[itr]) < 0.049
                            && Math.abs(upShadowPct[itr]) > Math.abs(downShadowPct[itr])
                            && MathHelper.isInRange(end[itr], pMa20[itr], pMa10[itr])
                            && MathHelper.isLessAbs(macdBar[itr - 1], macdBar[itr], 3.2)
                            && min[itr] < min[itr - 1]
                            ){
                        int maxIndex = mmPriceUtil5.getMaxIndex(itr);
                        double maxPrice = mmPriceUtil5.getMaxPrice(maxIndex);
                        if(MathStockUtil.calculateChangePct(max[itr], maxPrice) > 0.01d){
                            setBuy(4.3d, itr);
                        }
                    }
                }
            } else if(state == StockState.HoldStock && itr == sellIndex){
                score[itr] = -5d;
                setStateHoldMoney(false);
                sellIndex = -1;
            }
//            score[i] = lastSection.status1.ordinal();
        }
        setStateHoldMoney(true);
        addIndexData(INDEX_NAME, score);
    }

    private void setBuy(double currentScore, int idx){
        if(currentScore > score[idx]){
            score[idx] = currentScore;
            setStateHoldStock(currentScore);
            sellIndex = idx + 2;
        }
    }

    private boolean isMaBullForm(int i){
        return pMa60[i] < pMa20[i] && pMa20[i] < pMa10[i] && pMa10[i] < pMa5[i];
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
