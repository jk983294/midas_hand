package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.function.SectionalFunction;


import java.util.HashMap;

public class StockRevertScoreRank extends IndexCalcBase {

    public final static String INDEX_NAME = "score";

    static {
        IndexFactory.addCalculator(INDEX_NAME, new StockRevertScoreRank(IndexFactory.parameter));
    }

    private double[] end, start, max, min, volume, total, changePct;

    private double[] scores;

    private int len;

    public StockRevertScoreRank(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculator() {
        requiredCalculator.add(IndexChangePct.INDEX_NAME);
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        calculateScore();
        addIndexData(INDEX_NAME, scores);
    }

    private static final SectionalFunction volumeRatioFunc = new SectionalFunction(0.0d, 1d, 1d, 0d);
    private void calculateScore(){
        double score, volumeHigh, volumeRatio1, volumeRatio2;

        for (int i = 5; i < len; i++) {
            score = 0d;
            if(howManyDaysFall(i) == 2){
                volumeHigh = volume[i - 2];
                volumeRatio1 = volume[i - 1] / volumeHigh;
                volumeRatio2 = volume[i] / volumeHigh;
                score += volumeRatioFunc.calculate(volumeRatio1);
                score += volumeRatioFunc.calculate(volumeRatio2);
            }
            scores[i] = score;
        }
    }

    private int howManyDaysFall(int index){
        int current = index;
        for(; current >= 0; current--){
            if(MathStockUtil.calculateChangePct(start[current], end[current]) > 0d) break;
        }
        return index - current;
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        calculateFromScratch();
    }

    @Override
    protected void calculateForTrain() throws MidasException {
        calculateScore();
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        volume = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_VOLUME);
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);

        len = end.length;
        scores = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
    }

    @Override
    public void applyParameter() {
    }
}
