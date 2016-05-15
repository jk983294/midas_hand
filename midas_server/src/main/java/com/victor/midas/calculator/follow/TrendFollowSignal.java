package com.victor.midas.calculator.follow;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.divergence.IndexBadDepth;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.macd.IndexMACD;
import com.victor.midas.calculator.macd.model.MacdSectionType;
import com.victor.midas.calculator.util.LineBreakoutUtil;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.calculator.util.PriceLimitUtil;
import com.victor.midas.calculator.util.model.LineCrossSection;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.train.perf.DayStatistics;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import com.victor.utilities.utils.MathHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;

/**
 * calculate TrendFollowSignal
 */
public class TrendFollowSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "score_tfs";
    private MaBase maMethod = new SMA();

    private double[] score;
    private double[] dif, dea, macdBar;
    private double[] pMa5, pMa60, vMa5;
    private double[] middleShadowPct, upShadowPct, downShadowPct;
    private MaxMinUtil mmPriceUtil5;
    private int sellIndex, buyIndex;
    private StockState state;
    private PriceLimitUtil priceLimitUtil = new PriceLimitUtil(8);
    private LineBreakoutUtil lineBreakoutUtil = new LineBreakoutUtil();

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
        priceLimitUtil.init(end, start, max, min, changePct);
        pMa5 = maMethod.calculate(end, 5);
        pMa60 = maMethod.calculate(end, 60);
        vMa5 = maMethod.calculate(total, 5);
        lineBreakoutUtil.init(pMa5, pMa60);

        DescriptiveStatistics underMa5 = new DescriptiveStatistics();
        LineCrossSection currentSection, previousSection;
        boolean isBreakout = false;
        int breakoutIndex = 0, maxIndex, minIndex;
        long underMa5Cnt = 0;
        double avgChangePctUnderMa5 = 0d;

        int startIndex = -1;
        double startPrice = 0d;
        SimpleRegression regression = new SimpleRegression();
        DescriptiveStatistics changePctStats = new DescriptiveStatistics();

        sellIndex = -1;
        state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20150703){
//                System.out.println("wow");
//            }
            priceLimitUtil.updateStats(i);
            lineBreakoutUtil.update(i);
            currentSection = lineBreakoutUtil.currentSection;
            previousSection = lineBreakoutUtil.previousSection;

            if(start[i - 1] + end[i - 1] < pMa5[i - 1] * 2 && end[i] + start[i] > pMa5[i] * 2  && end[i] > pMa5[i]){
                isBreakout = true;
                breakoutIndex = i;
                currentSection.breakoutIndexes.add(breakoutIndex);
                underMa5Cnt = underMa5.getN();
                avgChangePctUnderMa5 = underMa5.getMean();
            }
            if(isBreakout && i - breakoutIndex > 4) isBreakout = false;
            if(end[i] + start[i] < pMa5[i] * 2){
                underMa5.addValue(changePct[i]);
            } else {
                underMa5.clear();
            }

            if(startIndex == mmPriceUtil5.getMinIndexRecursive(i)){
                regression.addData(i - startIndex, end[i] / startPrice);
                changePctStats.addValue(Math.abs(changePct[i]));
            } else if(end[i] > 0.1d){
                startIndex = mmPriceUtil5.getMinIndexRecursive(i);
                startPrice = end[i];
                regression.clear();
                regression.addData(0d, 1d);
                changePctStats.clear();
            }

            if(previousSection == null || currentSection == null) continue;

            if(state == StockState.HoldMoney) {
                if(startIndex > 0 && regression.getN() > 6 && regression.getN() < 23
                        && macdBar[startIndex] < 0d
                        && regression.getSlope() > 0d
                        && changePct[i] < 0d && changePct[i] > -0.03d
                        && middleShadowPct[i] < 0d && upShadowPct[i] < 0.02d
                        && total[i] < total[i - 1] && end[i] > pMa5[i]
                        && changePctStats.getMean() < 0.02d){
                    setBuy(4.6d, i);
                }
//                if (isBreakout
//                        && Math.min(start[i], end[i]) > pMa5[i]
//                        && changePct[i] < 0d
//                        && avgChangePctUnderMa5 > -0.028
//                        && total[i] < total[breakoutIndex]
//                        && Math.abs(middleShadowPct[breakoutIndex - 1]) < 0.02d
//                        ) {
//                    minIndex = mmPriceUtil5.getMaxIndex(breakoutIndex);
//                    if(breakoutIndex - minIndex < 2 && currentSection.type == MacdSectionType.green && underMa5Cnt > 5){
//                        maxIndex = mmPriceUtil5.getMaxIndex(minIndex);
//                        if(Math.abs(MathStockUtil.calculateChangePct(mmPriceUtil5.getMaxPrice(maxIndex), mmPriceUtil5.getMaxPrice(i))) > 0.02){
//                            setBuy(4.6d, i);
//                        }
//                    }
//                }
            } else if(state == StockState.HoldStock){
                if(i > buyIndex && macdBar[i] < macdBar[i - 1]){
                    score[i] = -5d;
                    state = StockState.HoldMoney;
                    sellIndex = -1;
                }
            }
//            score[i] = lastSection.status1.ordinal();
        }
        addIndexData(INDEX_NAME, score);
    }

    private boolean isDecrease(double[] value, int from, int to){
        for (int i = from + 1; i <= to; i++) {
            if(value[i] > value[i - 1]) return false;
        }
        return true;
    }

    private void setBuy(double currentScore, int idx){
        if(currentScore > score[idx]){
            score[idx] = currentScore;
            state = StockState.HoldStock;
            buyIndex = idx + 1;
            sellIndex = idx + 2;
        }
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
