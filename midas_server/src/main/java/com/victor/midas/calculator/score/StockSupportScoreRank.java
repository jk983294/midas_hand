package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasException;

public class StockSupportScoreRank extends IndexCalcBase {

    public final static String INDEX_NAME = "score_support";

    private double[] pricePositionScore;
    private double[] scores;

    public StockSupportScoreRank(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(PricePositionScore.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        calculateScore();
        addIndexData(INDEX_NAME, scores);
    }

    private void calculateScore(){
        double score;
        for (int i = 5; i < len; i++) {
            score = 0d;
            scores[i] = pricePositionScore[i];
        }
    }


    @Override
    protected void initIndex() throws MidasException {
        pricePositionScore = (double[])stock.queryCmpIndex("pricePositionScore");

        scores = new double[len];
    }

}
