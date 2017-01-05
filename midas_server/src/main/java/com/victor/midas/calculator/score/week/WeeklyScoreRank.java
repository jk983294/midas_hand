package com.victor.midas.calculator.score.week;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.JsonHelper;
import com.victor.utilities.utils.MathHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WeeklyScoreRank extends IndexCalcBase {

    public static final String INDEX_NAME = "weekly";

    private double[] score;

    private WeeklyDataUtil weeklyDataUtil = new WeeklyDataUtil();
    private List<WeeklyStockData> weeks;
    private WeeklyStockData previousWeeklyData, currentWeeklyData, minWeek, minWeekLeft, aboveMaxMaWeekLeft;
    private int weekCount, currentWeeklyDataIndex, buyWeekIndex, minIndex, minIndex1, maxIndex, aboveMaxMaWeekLeftIndex;
    private double minPrice, maxPrice;
    private double[] ma5, ma10, ma20, ma30;
    private MaxMinUtil mmWeekPriceUtil5;
    private MaxMinUtil mmWeekPriceUtil10;

    public WeeklyScoreRank(CalcParameter parameter) {
        super(parameter);
        mmWeekPriceUtil5 = new MaxMinUtil();
        mmWeekPriceUtil10 = new MaxMinUtil();
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        weeklyDataUtil.init(len, min, max, start, end, volume, total, dates, stock.getDatesDate());
        weeklyDataUtil.calculate();
        weeks = weeklyDataUtil.weeks;
        if(CollectionUtils.isEmpty(weeks)) return;
        mmWeekPriceUtil5.init(weeks);
        mmWeekPriceUtil5.calcMaxMinIndex(5);
        mmWeekPriceUtil10.init(weeks);
        mmWeekPriceUtil10.calcMaxMinIndex(10);
        weekCount = weeks.size();
        currentWeeklyDataIndex = 0;
        currentWeeklyData = previousWeeklyData = weeks.get(currentWeeklyDataIndex);
        ma5 = weeklyDataUtil.ma5;
        ma10 = weeklyDataUtil.ma10;
        ma20 = weeklyDataUtil.ma20;
        ma30 = weeklyDataUtil.ma30;

        double changPctLeft, changPctRight, changPctNow, changePctAboveMaxMa = 0d;

        state = StockState.HoldMoney;
        for (itr = 0; itr < len; itr++) {
//            if(dates[itr] == 20161230){
//                System.out.println("test");
//            }

            if(currentWeeklyData.cobTo < dates[itr]){
                previousWeeklyData = currentWeeklyData;
                ++currentWeeklyDataIndex;
                currentWeeklyData = weeks.get(currentWeeklyDataIndex);
            }

            // exclude new stock first 4 weeks, only decide in weekend
            if(currentWeeklyDataIndex <= 4 || !currentWeeklyData.isLastDayOfTheWeek(itr))
                continue;

            if(state == StockState.HoldMoney){
                minIndex = mmWeekPriceUtil5.getMinIndexRecursive(currentWeeklyDataIndex);
                maxIndex = mmWeekPriceUtil5.getMaxIndexRecursive(minIndex);
                minIndex1 = mmWeekPriceUtil5.getMinIndexRecursive(maxIndex);
                minWeek = weeks.get(minIndex);
                minWeekLeft = weeks.get(minIndex - 1 >= 0 ? minIndex - 1 : 0);
                changPctLeft = MathStockUtil.calculateChangePct(mmWeekPriceUtil5.getMinPrice(minIndex1), mmWeekPriceUtil5.getMaxPrice(maxIndex));
                changPctRight = MathStockUtil.calculateChangePct(mmWeekPriceUtil5.getMinPrice(minIndex), mmWeekPriceUtil5.getMaxPrice(maxIndex));
                changPctNow = MathStockUtil.calculateChangePct(mmWeekPriceUtil5.getMinPrice(minIndex1), currentWeeklyData.end);

                if(currentWeeklyData.aboveMaxMaWeekCount > 0){
                    aboveMaxMaWeekLeftIndex = currentWeeklyDataIndex - currentWeeklyData.aboveMaxMaWeekCount - 3;
                    aboveMaxMaWeekLeft = weeks.get(aboveMaxMaWeekLeftIndex >= 0 ? aboveMaxMaWeekLeftIndex : 0);
                    changePctAboveMaxMa = MathStockUtil.calculateChangePct(
                            mmWeekPriceUtil10.getMinPrice(mmWeekPriceUtil10.getMinIndexRecursive(mmWeekPriceUtil10.getMaxIndexRecursive(currentWeeklyDataIndex))),
                            currentWeeklyData.max);
                }


                if(previousWeeklyData.end < minOfPriceMa(previousWeeklyData.cobToIndex)
                        && currentWeeklyData.start > maxOfPriceMa(itr)
                        && currentWeeklyData.end > maxOfPriceMa(itr)
                        && MathStockUtil.calculateChangePct(previousWeeklyData.end, currentWeeklyData.end) < 0.36
                        && currentWeeklyData.min > minOfPriceMa(itr)
                        ){
                    //buyAction(3d);
                } else if((currentWeeklyData.end > currentWeeklyData.start || currentWeeklyData.changePct > 0d)
                        && minWeek.end < minOfPriceMa(minWeek.cobToIndex)
                        && minWeek.end < Math.min(minWeekLeft.end, minWeekLeft.start)
                        && minWeek.start < Math.min(minWeekLeft.end, minWeekLeft.start)
                        && minWeekLeft.changePct > -0.18
                        && currentWeeklyData.changePct < 0.19
                        && currentWeeklyDataIndex - minIndex <= 1
                        && currentWeeklyDataIndex - maxIndex > 2
                        ){
                    //buyAction(4d);
                } else if(
                        currentWeeklyData.aboveMaxMaWeekCount > 2
                        && currentWeeklyData.aboveMaxMaWeekCount < 11
                        && currentWeeklyData.maScore - aboveMaxMaWeekLeft.maScore > 3
                        && changePctAboveMaxMa < 0.24
                        ){
                    buyAction(5d + 0.01 * currentWeeklyData.aboveMaxMaWeekCount);
                }
            } else if(state == StockState.HoldStock){
                // hold four week, one month
                if(currentWeeklyDataIndex - buyWeekIndex >= 4){
                    score[itr] = -5d;
                    setStateHoldMoney();
                }
            }
        }
//        try {
//            FileUtils.write(new File("E:\\week.txt"), new JsonHelper().toJson(weeks));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        setStateHoldMoney();
        addIndexData(INDEX_NAME, score);
    }

    private void buyAction(double scoreValue){
        score[itr] = scoreValue;
        buyWeekIndex = currentWeeklyDataIndex;
        setStateHoldStock(scoreValue);
    }

    private double minOfPriceMa(int i){
        return MathHelper.min(ma5[i], ma10[i], ma20[i], ma30[i]);
    }

    private double maxOfPriceMa(int i){
        return MathHelper.max(ma5[i], ma10[i], ma20[i], ma30[i]);
    }

    @Override
    protected void initIndex() throws MidasException {
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
