package com.victor.midas.calculator.score;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.common.model.DirectionType;
import com.victor.midas.calculator.common.model.FractalType;
import com.victor.midas.calculator.common.model.TippingPoint;
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
    private DirectionType direction = DirectionType.Chaos;
    private final static int[] timeFrames = new int[]{5, 10, 20, 30, 60};
    private final static int timeFrameCnt = timeFrames.length;
    private List<TippingPoint> all = new ArrayList<>();
    private List<TippingPoint> tops = new ArrayList<>();
    private List<TippingPoint> bottoms = new ArrayList<>();
    private List<TippingPoint> topsFiltered = new ArrayList<>();
    private List<TippingPoint> bottomsFiltered = new ArrayList<>();

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
        double toBottomScore, toTopScore, firstBottomScore;

        SectionalFunction resistFunction = new SectionalFunction(-0.06d, 0d, 0, -1d, +0.06d, 0d);
        SectionalFunction supportFunction = new SectionalFunction(0.002 - 0.06d, 0d, 0.002, 1d, 0.002 + 0.06d, 0d);

        for(int i = 5; i < len; i++) {
//            if(dates[i] == 20150910){
//                System.out.println("wow");
//            }

            findTopsBottoms(i);
            filterTopBottom(i);
            decideCurrentDirection(i);
            if(all.size() >= 2 && direction != DirectionType.Chaos){
                SectionalFunction function = direction == DirectionType.Up ? resistFunction : supportFunction;

                toTopScore = toBottomScore = firstBottomScore = 0;

                if(topsFiltered.size() > 1 ){
                    for (int j = 0; j < topsFiltered.size(); j++) {
                        // when it touch previous top, it tends to decline
                        toTopScore += function.calculate(MathStockUtil.calculateChangePct(topsFiltered.get(j).price, maxMinUtils[0].getMaxPrice(i)));
                    }
                }
                if(bottomsFiltered.size() > 1){
                    for (int j = 0; j < bottomsFiltered.size(); j++) {
                        // don't use today's min, should use end price, it may have just touch the bottom, then bounce too high to get into trade
                        toBottomScore += function.calculate(MathStockUtil.calculateChangePct(bottomsFiltered.get(j).price, end[i]));
                    }
                }
//                if(MathStockUtil.calculateChangePct(bottoms.get(0).price, end[i]) > 0.2){
//                    firstBottomScore = function.calculate(MathStockUtil.calculateChangePct(bottoms.get(0).price, end[i]));
//                }

                if(DirectionType.Down == direction){
                    pricePositionScore[i] = toBottomScore;
                } else if(DirectionType.Up == direction){
                    pricePositionScore[i] = toTopScore;
                }
                pricePositionScore[i] = toBottomScore + toTopScore + firstBottomScore;
//              pricePositionScore[i] = direction.ordinal();
            }
        }

        // check market index, price position against price MA
        if(stock.getStockType() == StockType.Index){
        }

    }

    /**
     * first check previous direction, then compare current price position against nearest tipping point
     * if revert enough, then it considered the opposite direction
     */
    private void decideCurrentDirection(int i){
        direction = DirectionType.Chaos;
        double currentTrendChangePct, previousTrendChangePct;
        if(all.size() > 1){
            TippingPoint firstBottom = bottoms.get(0);
            TippingPoint firstTop = tops.get(0);
            direction = firstTop.cobIndex > firstBottom.cobIndex ? DirectionType.Up : DirectionType.Down;   // previous trend
            previousTrendChangePct = MathStockUtil.calculateChangePct(firstBottom.price, firstTop.price);
            if(DirectionType.Down == direction){
                currentTrendChangePct = MathStockUtil.calculateChangePct(maxMinUtils[0].getMinPrice(firstBottom.cobIndex), end[i]);
                if(Math.abs(currentTrendChangePct) > Math.abs(previousTrendChangePct) * 0.42){
                    direction = DirectionType.getOpposite(direction);
                }
            } else {
                currentTrendChangePct = MathStockUtil.calculateChangePct(end[i], maxMinUtils[0].getMaxPrice(firstTop.cobIndex));
                if(Math.abs(currentTrendChangePct) > Math.abs(previousTrendChangePct) * 0.3){
                    direction = DirectionType.getOpposite(direction);
                }
            }
        }
    }

    private void filterTopBottom(int i){
        double nearness, nearest;
        TippingPoint nearestRecord;
        topsFiltered.clear();
        bottomsFiltered.clear();
        if(tops.size() > 0){
            // find nearest tops
            nearestRecord = null;
            nearest = 100;
            for (int j = 0; j < tops.size(); j++) {
                nearness = Math.abs(MathStockUtil.calculateChangePct(maxMinUtils[0].getMaxPrice(i), tops.get(j).price));
                if(nearestRecord == null || nearness < nearest){
                    nearestRecord = tops.get(j);
                    nearest = nearness;
                }
            }
            if(nearestRecord != null) {
                // filter out those near nearest top
                for (int j = 0; j < tops.size(); j++) {
                    if (Math.abs(MathStockUtil.calculateChangePct(nearestRecord.price, tops.get(j).price)) < 0.05) {
                        topsFiltered.add(tops.get(j));
                    }
                }
            }
        }

        if(bottoms.size() > 0){
            // find nearest tops
            nearestRecord = null;
            nearest = 100;
            for (int j = 0; j < bottoms.size(); j++) {
                nearness = Math.abs(MathStockUtil.calculateChangePct(maxMinUtils[0].getMinPrice(i), bottoms.get(j).price));
                if(nearestRecord == null || nearness < nearest){
                    nearestRecord = bottoms.get(j);
                    nearest = nearness;
                }
            }
            if(nearestRecord != null){
                // filter out those near nearest top
                for (int j = 0; j < bottoms.size(); j++) {
                    if(Math.abs(MathStockUtil.calculateChangePct(nearestRecord.price, bottoms.get(j).price)) < 0.05){
                        bottomsFiltered.add(bottoms.get(j));
                    }
                }
            }
        }
    }

    private void findTopsBottoms(int i){
        int maxLookBackPeriod = 60;
        int minIndex, maxIndex, currentIndex, timeFrameIndex, topSize, bottomSize;
        boolean isCurrentBottom = false;

        timeFrameIndex = 0;
        all.clear();
        tops.clear();
        bottoms.clear();
        currentIndex = i;

        do {
            topSize = tops.size();
            bottomSize = bottoms.size();
            if(topSize == 0 && bottomSize == 0){
                currentIndex = i;
                minIndex = maxMinUtils[timeFrameIndex].getMinIndexRecursive(currentIndex, false);
                maxIndex = maxMinUtils[timeFrameIndex].getMaxIndexRecursive(currentIndex, false);
                if((minIndex == i && maxIndex == i) || minIndex == maxIndex){
                    timeFrameIndex++;
                } else if(minIndex == i && maxIndex < i){
                    isCurrentBottom = false;
                    currentIndex = maxIndex;
                    tops.add(new TippingPoint(currentIndex, all.size(), maxMinUtils[timeFrameIndex].getMaxPrice(currentIndex), FractalType.Top));
                    all.add(tops.get(tops.size() - 1));
                } else if(maxIndex == i && minIndex < i){
                    isCurrentBottom = true;
                    currentIndex = minIndex;
                    bottoms.add(new TippingPoint(currentIndex, all.size(), maxMinUtils[timeFrameIndex].getMinPrice(currentIndex), FractalType.Bottom));
                    all.add(bottoms.get(bottoms.size() - 1));
                } else if(minIndex < maxIndex){
                    isCurrentBottom = false;
                    currentIndex = maxIndex;
                    tops.add(new TippingPoint(currentIndex, all.size(), maxMinUtils[timeFrameIndex].getMaxPrice(currentIndex), FractalType.Top));
                    all.add(tops.get(tops.size() - 1));
                } else {
                    isCurrentBottom = true;
                    currentIndex = minIndex;
                    bottoms.add(new TippingPoint(currentIndex, all.size(), maxMinUtils[timeFrameIndex].getMinPrice(currentIndex), FractalType.Bottom));
                    all.add(bottoms.get(bottoms.size() - 1));
                }
            } else if(topSize > bottomSize || (topSize == bottomSize && !isCurrentBottom)){
                currentIndex = tops.get(topSize - 1).cobIndex;
                minIndex = maxMinUtils[timeFrameIndex].getMinIndexRecursive(currentIndex);
                if(minIndex == currentIndex){
                    timeFrameIndex++;
                } else {
                    isCurrentBottom = true;
                    currentIndex = minIndex;
                    bottoms.add(new TippingPoint(currentIndex, all.size(), maxMinUtils[timeFrameIndex].getMinPrice(currentIndex), FractalType.Bottom));
                    all.add(bottoms.get(bottoms.size() - 1));
                }
            } else if(topSize < bottomSize || (topSize == bottomSize && isCurrentBottom)){
                currentIndex = bottoms.get(bottomSize - 1).cobIndex;
                maxIndex = maxMinUtils[timeFrameIndex].getMaxIndexRecursive(currentIndex);
                if(maxIndex == currentIndex){
                    timeFrameIndex++;
                } else {
                    isCurrentBottom = false;
                    currentIndex = maxIndex;
                    tops.add(new TippingPoint(currentIndex, all.size(), maxMinUtils[timeFrameIndex].getMaxPrice(currentIndex), FractalType.Top));
                    all.add(tops.get(tops.size() - 1));
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
