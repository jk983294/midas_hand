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

public class StockRevertScoreRank extends IndexCalcBase {

    public final static String INDEX_NAME = "score_revert";

    private double[] upShadowPct, downShadowPct, middleShadowPct, badDepth;
    private double[] scores;

    private MaxMinUtil mmPriceUtil90, mmPriceUtil5;

    private double maxVolume, subMaxVolume, firstRevertVolume;
    private int maxVolumeIndex, subMaxVolumeIndex, firstRevertIndex, fallDays;
    private boolean hasFirstRevert, hasSubMaxVolume;

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
        requiredCalculators.add(IndexBadDepth.INDEX_NAME);
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
            if((changePct[i] < 0d || (middleShadowPct[i] < 0d && changePct[i] < 0.01)
                    || (changePct[i] < 0.01d && Math.abs(middleShadowPct[i]) < 0.01d))){
                changePctStats.addValue(changePct[i]);
            } else {
                changePctStats.clear();
            }

            fallDays = (int)changePctStats.getN();
            if(fallDays > 1){
                findMaxVolume(i);
                score += fallHeightScore(i);
                score += volumeScore(i);
                score += priceVolumeDivergenceScore(i);
                score += shadowScore(i);
                score += trapScore(i);
                //score += positionInHistoryMinMaxPriceScore(i);
            }
            scores[i] = score;
            // badDepth override
            if(badDepth[i] < -1d) scores[i] = badDepth[i];
        }
    }

    private double trapScore(int index){
        double score = 0d;
        score += starMiddleShadowTrapScore(index);
        score += volumeDownLossStopTrapScore(index);
        score += downShadowTrapScore(index);
        return score;
    }

    /**
     * when it is negative price stop, take its volume into consideration, the huger the volume is, the better
     */
    private static final SectionalFunction downShadowFunc = new SectionalFunction(0d, -1d, 20d, 0.75d);
    private double downShadowTrapScore(int index){
        boolean isDownShadowAlways = true;
        double score = 0d;
        int cnt = 0, downShadowCnt = 0, upShadowCnt = 0;
        for(int i = index - fallDays + 1; i < index; i++){
            if(downShadowPct[i] > Math.abs(middleShadowPct[i]) && downShadowPct[i] > 3 * upShadowPct[i]) downShadowCnt++;
            if(upShadowPct[i] > Math.abs(middleShadowPct[i]) && upShadowPct[i] > 3 * downShadowPct[i]) upShadowCnt++;
            score += downShadowFunc.calculate(Math.abs(middleShadowPct[i]));
            cnt++;
        }
        if(maxVolumeIndex == index && downShadowPct[index] > Math.abs(middleShadowPct[index])
                && downShadowPct[index] > 0.06){
            return -1d;
        }
        if(downShadowCnt == cnt && mmPriceUtil90.getMaxIndexPeriod(index) < 8
                && MathStockUtil.calculateChangePct(end[index], mmPriceUtil90.getMaxPrice(index)) < 0.1) return -1d;
        return 0d;
    }

    /**
     * when it is negative price stop, take its volume into consideration, the huger the volume is, the better
     */
    private static final SectionalFunction volumeDownLossStopFunc2 = new SectionalFunction(0d, -1d, 20d, 0.75d);
    private double volumeDownLossStopTrapScore(int index){
        if(MathStockUtil.isPriceStop(changePct[index]) && changePct[index] < 0d){
            return volumeDownLossStopFunc2.calculate(volume[index] / volume[index - 1]);
        }
        return 0;
    }

    /**
     * consecutive star k line, it means
     */
    private static final double STAR_SHADOW_THRESHOLD = 0.01d;
    private static final SectionalFunction starMiddleShadowFunc = new SectionalFunction(0d, -1d, STAR_SHADOW_THRESHOLD, 0d);
    private double starMiddleShadowTrapScore(int index){
        boolean isStarShadowAlways = true;
        double score = 0d;
        int cnt = 0;
        for(int i = index - fallDays + 1; i < index; i++){
            if(Math.abs(middleShadowPct[i]) > STAR_SHADOW_THRESHOLD){
                isStarShadowAlways = false;
                break;
            }
            score += starMiddleShadowFunc.calculate(Math.abs(middleShadowPct[i]));
            cnt++;
        }
        if(isStarShadowAlways) return score / (cnt <= 0 ? 1 : cnt);
        else return 0;
    }

    private static final SectionalFunction positionMaxFunc = new SectionalFunction(0.00, 0d, 0.3d, 1d);
    private double positionInHistoryMinMaxPriceScore(int index){
        double score = 0d;

        score += positionMaxFunc.calculate(MathStockUtil.calculateChangePct(end[index], mmPriceUtil90.getMaxPriceAmongTimeFrame(index)));
        return score / 1d;
    }

    private static final SectionalFunction shadowFunc1 = new SectionalFunction(0d, 0d, 0.1d, 1d);
    private double shadowScore(int index){
        double score = 0d;
        int cnt = 0;
        if(downShadowPct[index] > 0.05){
            score += shadowFunc1.calculate(downShadowPct[index]);
            cnt++;
        }
        if(fallDays > 1 && upShadowPct[index - 1] > 0.05){
            score += shadowFunc1.calculate(upShadowPct[index - 1]);
            cnt++;
        }
        return score / (cnt <= 0 ? 1 : cnt);
    }

    private static final SectionalFunction fallHeightFunc = new SectionalFunction(-0.2d, 0d, -0.033d, 1d, 0.1d, 0d);
    private double fallHeightScore(int index){
        double score = 0d;
        score += fallHeightFunc.calculate(changePctStats.getSum() / fallDays);
        return score;
    }

    private static final SectionalFunction volumeUpFunc = new SectionalFunction(0.8d, 0d, 1.5d, 1d, 3d, 0d);
    private static final SectionalFunction volumeDownFunc = new SectionalFunction(0.8d, 0d, 1.5d, 1d, 3d, 0d);
    private static final SectionalFunction volumeBenchFunc = new SectionalFunction(0d, 1d, 2d, 0d);
    private double volumeScore(int index){
        double score = 0d;
        int cnt = 0;
        for(int i = index - fallDays + 1; i < maxVolumeIndex; i++){
            score += (volumeUpFunc.calculate(volume[i + 1] / volume[i]) * 0.9);
            cnt++;
        }
        if(hasFirstRevert){
            for(int i = maxVolumeIndex; i < firstRevertIndex - 1; i++){
                score += (volumeDownFunc.calculate(volume[i] / volume[i + 1]) * 0.4);
                cnt++;
            }
            for(int i = firstRevertIndex - 1; i < subMaxVolumeIndex; i++){
                score += (volumeUpFunc.calculate(volume[i + 1] / volume[i]) * 0.1);
                cnt++;
            }
        }
        // if no first revert, means volume always decrease
        for(int i = subMaxVolumeIndex; i < index; i++){
            score += (volumeDownFunc.calculate(volume[i] / volume[i + 1]) * 1d);
            cnt++;
        }
//        double benchVolume = volume[index - fallDays];
//        double benchVolumeRatio = benchVolume / volume[index - fallDays - 1];
//        if(benchVolumeRatio < 1.5 && fallDays <= 2){
//            for(int i = index - fallDays + 1; i < index; i++){
//                score += volumeBenchFunc.calculate(volume[i] / benchVolume);
//                cnt++;
//            }
//        }
        return score / (cnt <= 0 ? 1 : cnt);
    }

    private static final SectionalFunction divergenceFunc1 = new SectionalFunction(0d, 1d, 0.3d, 0d);
    private static final SectionalFunction divergenceFunc2 = new SectionalFunction(0d, 0d, 0.3d, 1d);
    private double priceVolumeDivergenceScore(int index){
        double score = 0d, volumeRatio;
        int cnt = 0;
        for(int i = index - fallDays + 1; i < index; i++){
            volumeRatio = volume[i + 1] / volume[i];
            if(volumeRatio > 1){
                score += divergenceFunc1.calculate( Math.abs(changePct[i + 1]) / volumeRatio);
            } else {
                score += divergenceFunc2.calculate( Math.abs(changePct[i + 1]) / volumeRatio);
            }
            cnt++;
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
        hasFirstRevert = firstRevertIndex > maxVolumeIndex;
        hasSubMaxVolume = hasFirstRevert && (subMaxVolumeIndex > firstRevertIndex);
    }

    @Override
    protected void initIndex() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        badDepth = (double[])stock.queryCmpIndex("badDepth");

        mmPriceUtil90 = new MaxMinUtil(stock, true);
        mmPriceUtil90.calcMaxMinIndex(90);
        mmPriceUtil5 = new MaxMinUtil(stock, false);
        mmPriceUtil5.calcMaxMinIndex(5);

        scores = new double[len];
    }

}
