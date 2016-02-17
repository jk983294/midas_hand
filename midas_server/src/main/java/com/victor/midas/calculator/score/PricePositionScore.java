package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.function.SectionalFunction;
import com.victor.utilities.model.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * use max min to calculate the position score
 */
public class PricePositionScore extends IndexCalcBase {

    public final static String INDEX_NAME = "pricePositionScore";

    private double[] pricePositionScore;

    private double[] end, start, max, min, total;
    private double[] vMa5;
    private final static int[] timeFrames = new int[]{5, 10, 20, 30, 60};
    private final static int timeFrameCnt = timeFrames.length;
    private List<KeyValue<Integer, Double>> tops = new ArrayList<>();
    private List<KeyValue<Integer, Double>> bottoms = new ArrayList<>();
    private List<KeyValue<Integer, Double>> topsFiltered = new ArrayList<>();
    private List<KeyValue<Integer, Double>> bottomsFiltered = new ArrayList<>();

    private MaxMinUtil[] maxMinUtils;

    private int len;

    public PricePositionScore(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        calculateIndex();
        addIndexData("pricePositionScore", pricePositionScore);
    }

    private void calculateIndex() throws MidasException {
        int[] dates = stock.getDatesInt();
        double toBottomScore, toTopScore, volumeChange, nearness, nearest = 100;

        SectionalFunction topFunction = new SectionalFunction(-0.06d, 0d, 0d, -1d, 0.06d, 0d);
        SectionalFunction bottomFunction = new SectionalFunction(-0.08d, 0d, -0.02d, 1d, 0.04d, 0d);

        for(int i = 5; i < len; i++) {
//            if(dates[i] == 20151225){
//                System.out.println("wow");
//            }

            findTopsBottoms(i);
            filterTopBottom(i);

            toTopScore = toBottomScore = 0;
            if(topsFiltered.size() > 1 ){
                for (int j = 0; j < topsFiltered.size(); j++) {
                    // when it touch previous top, it tends to decline
                    toTopScore += topFunction.calculate(MathStockUtil.calculateChangePct(topsFiltered.get(j).getValue(), maxMinUtils[0].getMaxPrice(i)));
                }
            }
            if(bottomsFiltered.size() > 1){
                for (int j = 0; j < bottomsFiltered.size(); j++) {
                    // don't use today's min, should use end price, it may have just touch the bottom, then bounce too high to get into trade
                    toBottomScore += bottomFunction.calculate(MathStockUtil.calculateChangePct(bottomsFiltered.get(j).getValue(), end[i]));
                }
            }
            pricePositionScore[i] = toBottomScore + toTopScore;

//            changePctFromHigh = MathStockUtil.calculateChangePct(max[i], max[maxIndex]);
//            volumeChange = vMa5[i] / vMa5[maxIndex];
//            if(i - maxIndex > 10 && volumeChange < 0.8 && changePctFromHigh > 0d && changePctFromHigh < 0.03){
//                badDepth[i] += -5d;
//            }
        }

        // check market index, price position against price MA
        if(stock.getStockType() == StockType.Index){
//            MaBase maMethod = new SMA();
//            double[] ma = maMethod.calculate(end, 5);
//            double[] vMa = maMethod.calculate(total, 5);
//            for( int i = 5; i < len; i++) {
//                changePctFromMa = MathStockUtil.calculateChangePct(end[i], ma[i]);
//                volumeChange = MathStockUtil.calculateChangePct(vMa[i - 5], total[i]);
//                if(end[i] < ma[i] && changePctFromMa < 0.0091 ){
//                    pricePositionScore[i] = -5d;
//                }
//            }
        } else {


        }

    }

    private void filterTopBottom(int i){
        double nearness, nearest;
        KeyValue<Integer, Double> nearestRecord;
        topsFiltered.clear();
        bottomsFiltered.clear();
        if(tops.size() > 0){
            // find nearest tops
            nearestRecord = null;
            nearest = 100;
            for (int j = 0; j < tops.size(); j++) {
                nearness = Math.abs(MathStockUtil.calculateChangePct(maxMinUtils[0].getMaxPrice(i), tops.get(j).getValue()));
                if(nearestRecord == null || nearness < nearest){
                    nearestRecord = tops.get(j);
                    nearest = nearness;
                }
            }
            // filter out those near nearest top
            for (int j = 0; j < tops.size(); j++) {
                if(Math.abs(MathStockUtil.calculateChangePct(nearestRecord.getValue(), tops.get(j).getValue())) < 0.05){
                    topsFiltered.add(tops.get(j));
                }
            }
        }

        if(bottoms.size() > 0){
            // find nearest tops
            nearestRecord = null;
            nearest = 100;
            for (int j = 0; j < bottoms.size(); j++) {
                nearness = Math.abs(MathStockUtil.calculateChangePct(maxMinUtils[0].getMinPrice(i), bottoms.get(j).getValue()));
                if(nearestRecord == null || nearness < nearest){
                    nearestRecord = bottoms.get(j);
                    nearest = nearness;
                }
            }
            // filter out those near nearest top
            for (int j = 0; j < bottoms.size(); j++) {
                if(Math.abs(MathStockUtil.calculateChangePct(nearestRecord.getValue(), bottoms.get(j).getValue())) < 0.05){
                    bottomsFiltered.add(bottoms.get(j));
                }
            }
        }
    }

    private void findTopsBottoms(int i){
        int maxLookBackPeriod = 60;
        int minIndex, maxIndex, currentIndex, timeFrameIndex, topSize, bottomSize;
        boolean isCurrentBottom = false;

        timeFrameIndex = 0;
        tops.clear();
        bottoms.clear();
        currentIndex = i;

        do {
            topSize = tops.size();
            bottomSize = bottoms.size();
            if(topSize == 0 && bottomSize == 0){
                currentIndex = i;
                minIndex = maxMinUtils[timeFrameIndex].getMinIndexRecursive(currentIndex);
                maxIndex = maxMinUtils[timeFrameIndex].getMaxIndexRecursive(currentIndex);
                if(minIndex == i && maxIndex == i){
                    timeFrameIndex++;
                } else if(minIndex == i && maxIndex < i){
                    isCurrentBottom = false;
                    currentIndex = maxIndex;
                    tops.add(new KeyValue<>(currentIndex, maxMinUtils[timeFrameIndex].getMaxPrice(currentIndex)));
                } else if(maxIndex == i && minIndex < i){
                    isCurrentBottom = true;
                    currentIndex = minIndex;
                    bottoms.add(new KeyValue<>(currentIndex, maxMinUtils[timeFrameIndex].getMinPrice(currentIndex)));
                } else if(minIndex < maxIndex){
                    isCurrentBottom = false;
                    currentIndex = maxIndex;
                    tops.add(new KeyValue<>(currentIndex, maxMinUtils[timeFrameIndex].getMaxPrice(currentIndex)));
                } else {
                    isCurrentBottom = true;
                    currentIndex = minIndex;
                    bottoms.add(new KeyValue<>(currentIndex, maxMinUtils[timeFrameIndex].getMinPrice(currentIndex)));
                }
            } else if(topSize > bottomSize || (topSize == bottomSize && !isCurrentBottom)){
                currentIndex = tops.get(topSize - 1).getKey();
                minIndex = maxMinUtils[timeFrameIndex].getMinIndexRecursive(currentIndex);
                if(minIndex == currentIndex){
                    timeFrameIndex++;
                } else {
                    isCurrentBottom = true;
                    currentIndex = minIndex;
                    bottoms.add(new KeyValue<>(currentIndex, maxMinUtils[timeFrameIndex].getMinPrice(currentIndex)));
                }
            } else if(topSize < bottomSize || (topSize == bottomSize && isCurrentBottom)){
                currentIndex = bottoms.get(bottomSize - 1).getKey();
                maxIndex = maxMinUtils[timeFrameIndex].getMaxIndexRecursive(currentIndex);
                if(maxIndex == currentIndex){
                    timeFrameIndex++;
                } else {
                    isCurrentBottom = false;
                    currentIndex = maxIndex;
                    tops.add(new KeyValue<>(currentIndex, maxMinUtils[timeFrameIndex].getMaxPrice(currentIndex)));
                }
            }

            if(!(tops.size() == topSize && bottoms.size() == bottomSize)) timeFrameIndex = 0;
        } while (currentIndex > 0 && i - currentIndex < maxLookBackPeriod && timeFrameIndex < timeFrameCnt);
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        maxMinUtils = new MaxMinUtil[timeFrameCnt];
        for (int i = 0; i < timeFrameCnt; i++) {
            maxMinUtils[i] = new MaxMinUtil(stock, false);
            maxMinUtils[i].calcMaxMinIndex(timeFrames[i]);
        }
        len = end.length;

        pricePositionScore = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }
}
