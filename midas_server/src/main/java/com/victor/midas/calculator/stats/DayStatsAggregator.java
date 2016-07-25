package com.victor.midas.calculator.stats;

import com.victor.midas.calculator.common.AggregationCalcBase;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockDayStats;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.TopKElements;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import com.victor.utilities.model.KeyValue;
import com.victor.utilities.utils.ArrayHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * calculate day stats
 */
public class DayStatsAggregator extends AggregationCalcBase {

    public static final String INDEX_NAME = "dsa";

    public DayStatsAggregator(CalcParameter parameter) {
        super(parameter);
    }

    public List<StockDayStats> dayStatses = new ArrayList<>();

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
                    name2index.put(stockName, index + 1);       //advanceIndex
                }
            }
            dayStats.upPct = ArrayHelper.array2list(TopKElements.getFirstK(dayStats.upPct, 20));
            dayStatses.add(dayStats);
        }
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

}
