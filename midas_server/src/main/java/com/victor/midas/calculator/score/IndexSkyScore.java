package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;

/**
 * if price is go high than a long history's high, then it is very likely it will continue to go high
 */
public class IndexSkyScore extends IndexCalcBase {

    public static final String INDEX_NAME = "sky";

    private double[] score;

    public IndexSkyScore(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        double maxPriceSoFar = -9999.99, preMaxPriceSoFar = -9999.99;
        double minPriceAfterMaxPrice = 9999.99, preMinPriceAfterMaxPrice = 9999.99;
        int maxPriceSoFarIndex = -1, preMaxPriceSoFarIndex = -1;
        int minPriceAfterMaxPriceIndex = -1, preMinPriceAfterMaxPriceIndex = -1, buyIndex = -1;

        state = StockState.HoldMoney;
        for (itr = 5; itr < len; ++itr) {
//            if(dates[i] == 20160122){
//                System.out.println("debug");
//            }

            if(max[itr] > maxPriceSoFar){
                preMinPriceAfterMaxPrice = minPriceAfterMaxPrice;
                preMinPriceAfterMaxPriceIndex = minPriceAfterMaxPriceIndex;
                minPriceAfterMaxPrice = min[itr];
                minPriceAfterMaxPriceIndex = itr;

                preMaxPriceSoFar = maxPriceSoFar;
                preMaxPriceSoFarIndex = maxPriceSoFarIndex;
                maxPriceSoFar = max[itr];
                maxPriceSoFarIndex = itr;
            }

            if(maxPriceSoFarIndex > 0 && min[itr] < minPriceAfterMaxPrice){
                minPriceAfterMaxPrice = min[itr];
                minPriceAfterMaxPriceIndex = itr;
            }

            if(state == StockState.HoldMoney){
                if(maxPriceSoFarIndex == itr && maxPriceSoFarIndex > preMaxPriceSoFarIndex + 370
                        && MathStockUtil.calculateChangePct(preMinPriceAfterMaxPrice, maxPriceSoFar) < 1.1){
                    setStateHoldStock(5d);
                    buyIndex = itr;
                }
            } else if(state == StockState.HoldStock){
                // hold several days
                if(itr - buyIndex >= 40){
                    score[itr] = -5d;
                    setStateHoldMoney(false);
                }
            }
        }
        setStateHoldMoney(true);
        addIndexData(INDEX_NAME, score);
    }


    @Override
    protected void initIndex() throws MidasException {
        score = new double[len];
    }

    @Override
    public MidasTrainOptions getTrainOptions() {
        MidasTrainOptions options = new MidasTrainOptions();
        options.selectTops = false;
        options.useSignal = true;
        return options;
    }
}
