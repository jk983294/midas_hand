package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.function.SectionalFunction;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


import java.util.HashMap;

public class StockRevertScoreRank extends IndexCalcBase {

    public final static String INDEX_NAME = "score_revert";

    private double[] end, start, max, min, volume, total, changePct, upShadowPct, downShadowPct, middleShadowPct;

    private double[] scores;

    private int len;

    private double maxVolume, subMaxVolume, firstRevertVolume;
    private int maxVolumeIndex, subMaxVolumeIndex, firstRevertIndex, fallDays;

    private DescriptiveStatistics changePctStats = new DescriptiveStatistics();

    public StockRevertScoreRank(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        calculateScore();
        addIndexData(INDEX_NAME, scores);
    }

    private void calculateScore(){
        double score;
        changePctStats.clear();
        for (int i = 5; i < len; i++) {
            score = 0d;
            if(changePct[i] < 0d || middleShadowPct[i] < 0d || (changePct[i] < 0.01d && Math.abs(middleShadowPct[i]) < 0.01d)){
                changePctStats.addValue(changePct[i]);
            } else {
                changePctStats.clear();
            }

            fallDays = (int)changePctStats.getN();
            if(fallDays > 0){
                findMaxVolume(i);
                score += fallHeightScore(i);
                score += volumeScore(i);
            }
            scores[i] = score;
        }
    }



    private static final SectionalFunction fallHeightFunc = new SectionalFunction(-0.2d, 0d, -0.02d, 1d, 0.1d, 0d);
    private double fallHeightScore(int index){
        double score = 0d;
        score += fallHeightFunc.calculate(changePctStats.getSum() / fallDays);
        return score;
    }

    private static final SectionalFunction volumeUpFunc = new SectionalFunction(0.8d, 0d, 1.5d, 1d, 3d, 0d);
    private static final SectionalFunction volumeDownFunc = new SectionalFunction(0.8d, 0d, 1.5d, 1d, 3d, 0d);
    private double volumeScore(int index){
        double score = 0d;
        int cnt = 0;
        for(int i = index - fallDays + 1; i < maxVolumeIndex; i++){
            score += volumeUpFunc.calculate(volume[i + 1] / volume[i]);
            cnt++;
        }
        if(maxVolumeIndex < subMaxVolumeIndex){
            for(int i = maxVolumeIndex; i < subMaxVolumeIndex; i++){
                score += volumeDownFunc.calculate(volume[i] / volume[i + 1]);
                cnt++;
            }
            for(int i = subMaxVolumeIndex; i < index; i++){
                score += volumeUpFunc.calculate(volume[i + 1] / volume[i]);
                cnt++;
            }
        } else {
            for(int i = subMaxVolumeIndex; i < index; i++){
                score += volumeDownFunc.calculate(volume[i] / volume[i + 1]);
                cnt++;
            }
        }
        return score / (cnt <= 0 ? 1 : cnt);
    }

    private void findMaxVolume(int index){
        maxVolumeIndex = subMaxVolumeIndex = index - fallDays + 1;
        maxVolume = subMaxVolume = volume[maxVolumeIndex];
        for(int i = maxVolumeIndex; i <= index; i++){
            if(volume[i] > maxVolume){
                maxVolume = volume[i];
                maxVolumeIndex = i;
            }
        }
        // sub max index must after max index, find first volume revert point
        subMaxVolume = firstRevertVolume = maxVolume;
        subMaxVolumeIndex = firstRevertIndex = maxVolumeIndex;
        for(int i = maxVolumeIndex + 1; i <= index; i++){
            if(volume[i] > volume[i - 1]){
                subMaxVolume = firstRevertVolume = volume[i];
                subMaxVolumeIndex = firstRevertIndex = i;
                break;
            }
        }
        // after find the first volume revert point, volume could have sub max after that point
        if(firstRevertIndex > maxVolumeIndex){
            for(int i = firstRevertIndex + 1; i <= index; i++){
                if(volume[i] > subMaxVolume){
                    subMaxVolume = volume[i];
                    subMaxVolumeIndex = i;
                }
            }
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
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");

        len = end.length;
        scores = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }
}
