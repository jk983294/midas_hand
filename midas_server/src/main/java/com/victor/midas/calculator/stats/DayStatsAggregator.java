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
        double[] upTrendPct, upTrendTime, downTrendPct, downTrendTime;
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
                    upTrendPct = (double[])stock.queryCmpIndex("upTrendPct");
                    upTrendTime = (double[])stock.queryCmpIndex("upTrendTime");
                    downTrendPct = (double[])stock.queryCmpIndex("downTrendPct");
                    downTrendTime = (double[])stock.queryCmpIndex("downTrendTime");
                    dayStats.upPct.add(new KeyValue<>(upTrendPct[index], stockName));
                    dayStats.downPct.add(new KeyValue<>(downTrendPct[index], stockName));
                    dayStats.upPctTime.add(new KeyValue<>(upTrendTime[index], stockName));
                    dayStats.downPctTime.add(new KeyValue<>(downTrendTime[index], stockName));
                    name2index.put(stockName, index + 1);       //advanceIndex
                }
            }
            dayStats.upPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.upPct, wantedCount));
            Collections.sort(dayStats.upPct);
            dayStats.downPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.downPct, wantedCount));
            Collections.sort(dayStats.downPct);
            dayStats.upPctTime = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.upPctTime, wantedCount));
            Collections.sort(dayStats.upPctTime);
            dayStats.downPctTime = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.downPctTime, wantedCount));
            Collections.sort(dayStats.downPctTime);
            dayStatses.add(dayStats);
        }
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

}
