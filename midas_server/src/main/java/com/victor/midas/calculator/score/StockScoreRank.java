package com.victor.midas.calculator.score;

import com.victor.midas.calculator.chan.ChanMorphology;
import com.victor.midas.calculator.chan.ChanMorphologyExtend;
import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.*;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.function.SectionalFunction;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;

/**
 * Price Moving Average Tangle Up state, like pMa5 > pMa10 > pMa20 > pMa30 > pMa60
 */
public class StockScoreRank extends IndexCalcBase {

    public static final String INDEX_NAME = "ssr";

    static {
        IndexFactory.addCalculator(INDEX_NAME, new StockScoreRank(IndexFactory.parameter));
    }

    @Override
    public void setRequiredCalculator() {
        requiredCalculator.add(IndexChangePct.INDEX_NAME);
    }

    private MaBase maMethod = new SMA();
    private MathDeltaUtil deltaUtil = new MathDeltaUtil(6);
    private MaxMinUtil mmPriceUtil10, mmPriceUtil60;
    private MaxMinVolumeUtil mmVolumeUtil10, mmVolumeUtil60;
    private static final SectionalFunction endPriceClose2MaFunc = new SectionalFunction(-0.01, 0d, 0.03, 1d, 0.07, 0d);
    private static final SectionalFunction minPriceClose2MaFunc = new SectionalFunction(-0.02, 0d, 0.02, 1d, 0.06, 0d);
    private static final SectionalFunction maCloseFunc = new SectionalFunction(-0.07, 0d, 0.00, 1d, 0.07, 0d);

    private static final ChanMorphology chanMorphology = new ChanMorphology(new CalcParameter());

    private double[] end, start, max, min, avgVolume, total, changePct;
    private double[] pMa5, pMa10, pMa20, pMa30, pMa60, pMa120, pMa250;
    private double[] pMa5D1, pMa10D1, pMa20D1, pMa30D1, pMa60D1, pMa120D1, pMa250D1;
    private double[] avgAmplitude;
    private double[] cme;

    private double[] scores;

    private int len;

    public StockScoreRank(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        calculateScore();

        avgAmplitude = maMethod.calculate(MathStockUtil.calculateChangePct(min, max, 0), 45);
        addIndexData("ssr", scores);
        addIndexData("avgAmplitude", avgAmplitude);
//        addIndexData("pMa5D1", pMa5D1);
//        addIndexData("pMa250D1", pMa250D1);
    }

    private void calculateScore(){
        double score;
        DescriptiveStatistics scoreStats = new DescriptiveStatistics();
        scoreStats.setWindowSize(3);
        for (int i = 5; i < len; i++) {
            score = 0d;

            score += priceCloseToMaScore(i) * 2;
            score += maLinageScore(i);
            score += maTangleScore(i);
            score += positionInHistoryMinMaxPriceScore(i);
            scoreStats.addValue(priceScore(i));
            score += scoreStats.getMean();
            score += positionInHistoryMinMaxVolumeScore(i);
//            score += cme[i] * 1d;
            //score += revertScore(i);
            /**perf not good, so comment out*/
//            score += maTrendScore(i);
//            score += changPctTrendScore(i);
            scores[i] = score;
        }
    }

    /**
     * measure how close of different time frame MAs
     */
    private double priceCloseToMaScore(int index){
        double score = 0d;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], end[index])) * 1.6;
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], min[index])) * 1.6;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], end[index])) * 1.5;
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], min[index])) * 1.5;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa60[index], end[index])) * 1.4;
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa60[index], min[index])) * 1.4;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa30[index], end[index])) * 1.3;
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa30[index], min[index])) * 1.3;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa20[index], end[index])) * 1.2;
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa20[index], min[index])) * 1.2;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa10[index], end[index])) * 1.1;
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa10[index], min[index])) * 1.1;
        score += endPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa5[index], end[index]));
        score += minPriceClose2MaFunc.calculate(MathStockUtil.calculateChangePct(pMa5[index], min[index]));
        return score / 12d;
    }

    /**
     * measure how close of different time frame MAs
     */
    private double maTangleScore(int index){
        double score = 0d;
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], pMa120[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], pMa60[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], pMa30[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], pMa20[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], pMa10[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa250[index], pMa5[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], pMa60[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], pMa30[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], pMa20[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], pMa10[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa120[index], pMa5[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa60[index], pMa30[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa60[index], pMa20[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa60[index], pMa10[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa60[index], pMa5[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa30[index], pMa20[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa30[index], pMa10[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa30[index], pMa5[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa20[index], pMa10[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa20[index], pMa5[index]));
        score += maCloseFunc.calculate(MathStockUtil.calculateChangePct(pMa10[index], pMa5[index]));
        return score / 21d;
    }

    /**
     * measure sequence of different time frame MAs
     */
    private double maLinageScore(int index){
        double score = 0d;
        if(pMa120[index] > pMa250[index]) score += 1.5d;
        if(pMa60[index] > pMa120[index]) score += 1.4d;
        if(pMa30[index] > pMa60[index]) score += 1.3d;
        if(pMa20[index] > pMa30[index]) score += 1.2d;
        if(pMa10[index] > pMa20[index]) score += 1.1d;
        if(pMa5[index] > pMa10[index]) score += 1d;
        return score / 6;
    }

    /**
     * measure how good of different time frame MAs,
     * using MA derivative
     */
    private static final SectionalFunction maD1CloseFunc = new SectionalFunction(-2d, 0d, 0.00, 1d, 30d, 0d);
    private static final SectionalFunction maD1CloseFunc1 = new SectionalFunction(0.50, 1d, 30d, 0d);
    private double maTrendScore(int index){
        double score = 0d;
        score += maD1CloseFunc1.calculate(1000d * pMa250D1[index] / end[index]);
        score += maD1CloseFunc1.calculate(1000d * pMa120D1[index] / end[index]);
        score += maD1CloseFunc1.calculate(1000d * pMa60D1[index] / end[index]);
        score += maD1CloseFunc.calculate(1000d * pMa30D1[index] / end[index]);
        score += maD1CloseFunc.calculate(1000d * pMa20D1[index] / end[index]);
        score += maD1CloseFunc.calculate(1000d * pMa10D1[index] / end[index]);
        score += maD1CloseFunc.calculate(1000d * pMa5D1[index] / end[index]);
        return score / 7d;
    }

    private static final SectionalFunction changePctFunc1 = new SectionalFunction(0d, 0d, 2.5d, 1d);
    private static final SectionalFunction changePctFunc2 = new SectionalFunction(0d, 0d, 3d, 1d, 7d, 0d);
    private static final SectionalFunction changePctFunc3 = new SectionalFunction(-0.04d, 0d, 0.00, 1d, 0.04d, 0d);
    private double changPctTrendScore(int index){
        double score = 0d;
        int dayCnt = 0;
        for(int i = index; i >= 1; i--){
            if(Math.abs(changePct[i]) < 0.04){
                score += changePctFunc3.calculate(MathStockUtil.calculatePct(end[i - 1], end[i] - start[i]));
                ++dayCnt;
            } else {
                break;
            }
        }
        score = changePctFunc1.calculate(score) * changePctFunc2.calculate(dayCnt);
        return score;
    }

    private static final SectionalFunction positionMaxFunc = new SectionalFunction(0.00, 0d, 0.2d, 1d);
    private static final SectionalFunction positionMinFunc = new SectionalFunction(0.00, 1d, 0.2d, 0d);
    private static final SectionalFunction positionRangeFunc = new SectionalFunction(0.00, 1d, 0.4d, 0d);
    private double positionInHistoryMinMaxPriceScore(int index){
        double score = 0d;
        score += positionMinFunc.calculate(MathStockUtil.calculateChangePct(mmPriceUtil10.getFewDaysBeforeMinPrice(index - 1), end[index]));
        score += positionMaxFunc.calculate(MathStockUtil.calculateChangePct(end[index], mmPriceUtil10.getFewDaysBeforeMaxPrice(index - 1)));
        score += positionMinFunc.calculate(MathStockUtil.calculateChangePct(mmPriceUtil60.getFewDaysBeforeMinPrice(index - 1), end[index]));
        score += positionMaxFunc.calculate(MathStockUtil.calculateChangePct(end[index], mmPriceUtil60.getFewDaysBeforeMaxPrice(index - 1)));
        score += positionRangeFunc.calculate(MathStockUtil.calculateChangePct(mmPriceUtil10.getFewDaysBeforeMinPrice(index - 1), mmPriceUtil10.getFewDaysBeforeMaxPrice(index - 1)));
        score += positionRangeFunc.calculate(MathStockUtil.calculateChangePct(mmPriceUtil60.getFewDaysBeforeMinPrice(index - 1), mmPriceUtil60.getFewDaysBeforeMaxPrice(index - 1)));
        return score / 6d;
    }

    private static final SectionalFunction positionMaxVolumeFunc = new SectionalFunction(0.00, 0d, 0.2d, 1d);
    private static final SectionalFunction positionMinVolumeFunc = new SectionalFunction(0.00, 1d, 0.2d, 0d);
    private double positionInHistoryMinMaxVolumeScore(int index){
        double score = 0d;
        score += positionMinVolumeFunc.calculate(MathStockUtil.calculateChangePct(mmVolumeUtil10.getFewDaysBeforeMinVolume(index - 1), avgVolume[index]));
        score += positionMaxVolumeFunc.calculate(MathStockUtil.calculateChangePct(avgVolume[index], mmVolumeUtil10.getFewDaysBeforeMaxVolume(index - 1)));
        score += positionMinVolumeFunc.calculate(MathStockUtil.calculateChangePct(mmVolumeUtil60.getFewDaysBeforeMinVolume(index - 1), avgVolume[index]));
        score += positionMaxVolumeFunc.calculate(MathStockUtil.calculateChangePct(avgVolume[index], mmVolumeUtil10.getFewDaysBeforeMaxVolume(index - 1)));
        return score / 4d;
    }


    /**
     * price should not fluctuate too much, otherwise it is abnormal
     */
    private static final SectionalFunction pricePositionFunc = new SectionalFunction(0.03, 1d, 0.1d, 0d);
    private double priceScore(int index){
        double score = 0d;
        score += pricePositionFunc.calculate(Math.abs(MathStockUtil.calculateChangePct(end[index - 1], max[index])));
        score += positionMaxVolumeFunc.calculate(Math.abs(MathStockUtil.calculateChangePct(end[index - 1], min[index])));
        score += pricePositionFunc.calculate(Math.abs(MathStockUtil.calculateChangePct(end[index - 1], start[index])));
        score += positionMaxVolumeFunc.calculate(Math.abs(MathStockUtil.calculateChangePct(end[index - 1], end[index])));
        return score / 4d;
    }

    private DescriptiveStatistics revertScoreStats = new DescriptiveStatistics();
    private static final SectionalFunction revertScoreFunc = new SectionalFunction(0.01, 0d, 2d, 1d);
    private double revertScore(int index){
        double score = 0d, totalChangePct = 0d;
        int downDays = 0;
        for(int i = index; i >= 0; i--){
            if(changePct[i] < 0){
                ++downDays;
                totalChangePct += changePct[i];
            } else {
                break;
            }
        }
        if(downDays > 3 && totalChangePct < -0.35 ){
            revertScoreStats.clear();
            for(int i = index - downDays + 1; i < index; i++){
                revertScoreStats.addValue(avgVolume[i]);
                score += Math.abs(changePct[i]) / (avgVolume[i] / revertScoreStats.getMean());
            }
            revertScoreStats.addValue(avgVolume[index]);
            score += Math.abs(changePct[index]) * (avgVolume[index] / revertScoreStats.getMean());
        }
        return revertScoreFunc.calculate(score) * 0.6;
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
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);

        initData();

        len = end.length;
        scores = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

    private void initData() throws MidasException {
        pMa5 = maMethod.calculate(end, 5);
        pMa10 = maMethod.calculate(end, 10);
        pMa20 = maMethod.calculate(end, 20);
        pMa30 = maMethod.calculate(end, 30);
        pMa60 = maMethod.calculate(end, 60);
        pMa120 = maMethod.calculate(end, 120);
        pMa250 = maMethod.calculate(end, 250);

        pMa5D1 = deltaUtil.calculate(pMa5, false);
        pMa10D1 = deltaUtil.calculate(pMa10, false);
        pMa20D1 = deltaUtil.calculate(pMa20, false);
        pMa30D1 = deltaUtil.calculate(pMa30, false);
        pMa60D1 = deltaUtil.calculate(pMa60, false);
        pMa120D1 = deltaUtil.calculate(pMa120, false);
        pMa250D1 = deltaUtil.calculate(pMa250, false);

        mmPriceUtil10 = new MaxMinUtil(stock);
        mmPriceUtil10.calcMaxMinIndex(10);
        mmPriceUtil60 = new MaxMinUtil(stock);
        mmPriceUtil60.calcMaxMinIndex(60);

        avgVolume = MathStockUtil.calcAvgVolume(end, start, total);
        mmVolumeUtil10 = new MaxMinVolumeUtil(avgVolume);
        mmVolumeUtil10.calcMaxMinIndex(10);
        mmVolumeUtil60 = new MaxMinVolumeUtil(avgVolume);
        mmVolumeUtil60.calcMaxMinIndex(60);

        ChanMorphologyExtend chanMorphologyExtend = new ChanMorphologyExtend(new CalcParameter());
        chanMorphologyExtend.calculate(stock);
        cme = (double[])stock.queryCmpIndex("cme");
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
    }

    @Override
    public void applyParameter() {
    }
}
