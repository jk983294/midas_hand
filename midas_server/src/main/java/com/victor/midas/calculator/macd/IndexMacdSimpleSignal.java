package com.victor.midas.calculator.macd;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;

/**
 * calculate MACD
 */
public class IndexMacdSimpleSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "macd_simple_signal";

    private double[] dif, dea, macdBar; // white line, yellow line, bar
    private double[] score;

    public IndexMacdSimpleSignal(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexMACD.INDEX_NAME);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        StockState state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20150818){
//                System.out.println("test");
//            }
            if(state == StockState.HoldMoney && macdBar[i] < 0d && macdBar[i - 1] < macdBar[i]){
                score[i] = 5d;
                state = StockState.HoldStock;
            } else if(state == StockState.HoldStock && macdBar[i - 1] > macdBar[i]){
                score[i] = -5d;
                state = StockState.HoldMoney;
            }
        }
        addIndexData(INDEX_NAME, score);
    }

    @Override
    protected void initIndex() throws MidasException {
        dif = (double[])stock.queryCmpIndex("dif");
        dea = (double[])stock.queryCmpIndex("dea");
        macdBar = (double[])stock.queryCmpIndex("macdBar");
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
