package com.victor.midas.calculator.stats;

import com.victor.midas.calculator.common.AggregationCalcBase;
import com.victor.midas.calculator.score.DayStatsScore;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockDayStats;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.TopKElements;
import com.victor.utilities.model.KeyValue;
import com.victor.utilities.utils.ArrayHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * calculate day stats
 */
public class DayStatsAggregator extends AggregationCalcBase {

    public static final String INDEX_NAME = "dsa";

    public static final int wantedCount = 8;

    public DayStatsAggregator(CalcParameter parameter) {
        super(parameter);
    }

    public List<StockDayStats> dayStatses = new ArrayList<>();

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(DayStatsScore.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        double[] longTermUpPct, longTermDownPct, shortTermUpPct, shortTermDownPct, upSlow, downFast;
        int marketCob;
        String stockName;
        for (int i = 0; i < len; i++) {
            marketCob = dates[i];
            StockDayStats dayStats = new StockDayStats(marketCob);
            for (int j = 0; j < tradableCnt; j++) {
                StockVo stock = tradableStocks.get(j);
                stockName = stock.getStockName();
                index = name2index.get(stockName);
                if(stock.isSameDayWithIndex(marketCob, index)){
                    longTermUpPct = (double[])stock.queryCmpIndex("longTermUpPct");
                    longTermDownPct = (double[])stock.queryCmpIndex("longTermDownPct");
                    shortTermUpPct = (double[])stock.queryCmpIndex("shortTermUpPct");
                    shortTermDownPct = (double[])stock.queryCmpIndex("shortTermDownPct");
                    upSlow = (double[])stock.queryCmpIndex("upSlow");
                    downFast = (double[])stock.queryCmpIndex("downFast");
                    dayStats.longTermUpPct.add(new KeyValue<>(longTermUpPct[index], stockName));
                    dayStats.longTermDownPct.add(new KeyValue<>(longTermDownPct[index], stockName));
                    dayStats.shortTermUpPct.add(new KeyValue<>(shortTermUpPct[index], stockName));
                    dayStats.shortTermDownPct.add(new KeyValue<>(shortTermDownPct[index], stockName));
                    dayStats.upSlow.add(new KeyValue<>(upSlow[index], stockName));
                    dayStats.downFast.add(new KeyValue<>(downFast[index], stockName));
                    name2index.put(stockName, index + 1);       //advanceIndex
                }
            }
            dayStats.longTermUpPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.longTermUpPct, wantedCount));
            Collections.sort(dayStats.longTermUpPct);
            dayStats.longTermDownPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.longTermDownPct, wantedCount));
            Collections.sort(dayStats.longTermDownPct);
            dayStats.shortTermUpPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.shortTermUpPct, wantedCount));
            Collections.sort(dayStats.shortTermUpPct);
            dayStats.shortTermDownPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.shortTermDownPct, wantedCount));
            Collections.sort(dayStats.shortTermDownPct);
            dayStats.upSlow = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.upSlow, wantedCount));
            Collections.sort(dayStats.upSlow);
            dayStats.downFast = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.downFast, wantedCount));
            Collections.sort(dayStats.downFast);
            dayStatses.add(dayStats);
        }
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

}
