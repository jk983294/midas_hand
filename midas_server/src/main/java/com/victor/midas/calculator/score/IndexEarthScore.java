package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;

/**
 * if price is go low closing to a long history's low, then it is very likely it will revert
 */
public class IndexEarthScore extends IndexCalcBase {

    public static final String INDEX_NAME = "earth";

    private double[] score;

    public IndexEarthScore(CalcParameter parameter) {
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
        double minPriceSoFar = 9999.99, preMinPriceSoFar = 9999.99;
        double maxPriceAfterMinPrice = -9999.99, preMaxPriceAfterMinPrice = -9999.99;
        int minPriceSoFarIndex = -1, preMinPriceSoFarIndex = -1;
        int maxPriceAfterMinPriceIndex = -1, preMaxPriceAfterMinPriceIndex = -1, buyIndex = -1;
        int minTimeFrame = 314;

        state = StockState.HoldMoney;
        for (itr = 5; itr < len; ++itr) {
//            if(dates[i] == 20160122){
//                System.out.println("debug");
//            }

            if(min[itr] < minPriceSoFar){
                preMaxPriceAfterMinPrice = maxPriceAfterMinPrice;
                preMaxPriceAfterMinPriceIndex = maxPriceAfterMinPriceIndex;
                maxPriceAfterMinPrice = min[itr];
                maxPriceAfterMinPriceIndex = itr;

                if(preMinPriceSoFarIndex < 0 || itr > minPriceSoFarIndex + minTimeFrame){
                    preMinPriceSoFar = minPriceSoFar;
                    preMinPriceSoFarIndex = minPriceSoFarIndex;
                }

                minPriceSoFar = min[itr];
                minPriceSoFarIndex = itr;
            }

            if(minPriceSoFarIndex > 0 && max[itr] > maxPriceAfterMinPrice){
                maxPriceAfterMinPrice = max[itr];
                maxPriceAfterMinPriceIndex = itr;
            }

            if(state == StockState.HoldMoney){
                if(minPriceSoFarIndex == itr && minPriceSoFarIndex > preMinPriceSoFarIndex + minTimeFrame
                        && MathStockUtil.calculateChangePct(preMinPriceSoFar, minPriceSoFar) < -0.02){
                    setStateHoldStock(5d);
                    buyIndex = itr;
                }
            } else if(state == StockState.HoldStock){
                // hold several days
                if(itr - buyIndex >= 98){
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
