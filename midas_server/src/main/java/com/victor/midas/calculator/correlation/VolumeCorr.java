package com.victor.midas.calculator.correlation;

import com.victor.midas.calculator.common.AggregationCalcbase;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * calculate volume correlation with whole info
 */
public class VolumeCorr extends AggregationCalcbase {

    private double[] avgTotal;

    public VolumeCorr(List<StockVo> stocks, StockVo indexSH, List<StockVo> tradableStocks) {
        super(stocks, indexSH, tradableStocks);
        avgTotal = new double[len];
    }

    @Override
    public void calculate() throws MidasException {
        double[] stockIndexTotal;
        double totalAll;
        int dateSH, currentDayTradableCnt;
        String stockName;
        for (int i = 0; i < len; i++) {
            dateSH = dates[i];
            totalAll = 0;
            currentDayTradableCnt = 0;
            for (int j = 0; j < tradableCnt; j++) {
                StockVo stock = tradableStocks.get(j);
                stockName = stock.getStockName();
                index = name2index.get(stockName);
                if(stock.isSameDayWithIndex(dateSH, index)){
                    stockIndexTotal = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
                    totalAll += stockIndexTotal[index];
                    name2index.put(stockName, index + 1);       //advanceIndex
                    ++currentDayTradableCnt;
                }
            }
            if(currentDayTradableCnt > 0){
                avgTotal[i] = totalAll / currentDayTradableCnt;
            }
        }

        MaBase maMethod = new SMA();
        avgTotal = maMethod.calculate(avgTotal, 30);
    }

    @Override
    public void applyParameter() {

    }

    public double[] getAvgTotal() {
        return avgTotal;
    }

    public void setAvgTotal(double[] avgTotal) {
        this.avgTotal = avgTotal;
    }
}
