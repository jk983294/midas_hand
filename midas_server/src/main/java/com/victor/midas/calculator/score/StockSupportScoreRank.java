package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.divergence.IndexBadDepth;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.function.SectionalFunction;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;

public class StockSupportScoreRank extends IndexCalcBase {

    public final static String INDEX_NAME = "score_support";

    private double[] end, start, max, min, volume, total, changePct, badDepth, pricePositionScore;
    private double[] scores;
    private int len;

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
        requiredCalculators.add(IndexBadDepth.INDEX_NAME);
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
            // badDepth override
//            if(badDepth[i] < -1d) scores[i] = badDepth[i];
        }
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
        badDepth = (double[])stock.queryCmpIndex("badDepth");
        pricePositionScore = (double[])stock.queryCmpIndex("pricePositionScore");

        len = end.length;
        scores = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

}
