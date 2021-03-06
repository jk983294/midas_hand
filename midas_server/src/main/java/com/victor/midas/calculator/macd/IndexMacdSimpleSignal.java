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
        state = StockState.HoldMoney;
        for (itr = 5; itr < len; itr++) {
//            if(dates[i] == 20150818){
//                System.out.println("test");
//            }
            if(state == StockState.HoldMoney && macdBar[itr] < 0d && macdBar[itr - 1] < macdBar[itr]){
                score[itr] = 5d;
                setStateHoldStock(score[itr]);
            } else if(state == StockState.HoldStock && macdBar[itr - 1] > macdBar[itr]){
                score[itr] = -5d;
                setStateHoldMoney(false);
            }
        }
        setStateHoldMoney(true);
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
