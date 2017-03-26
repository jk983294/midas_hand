package com.victor.midas.calculator.stats;

import com.victor.midas.calculator.common.AggregationCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;

import java.util.HashMap;
import java.util.Map;


/**
 * calculate aggregated index
 */
public class GlobalIndex extends AggregationCalcBase {

    public static final String INDEX_NAME = "global";

    public GlobalIndex(CalcParameter parameter) {
        super(parameter);
    }

    private MaBase maBase = new SMA();

    private Map<String, MaxMinUtil> stock2maxMinUtil;
    private MaxMinUtil maxMinUtil;

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        stock2maxMinUtil = new HashMap<>();
        for (int j = 0; j < tradableCnt; j++) {
            StockVo stock = tradableStocks.get(j);
            MaxMinUtil mmPriceUtil = new MaxMinUtil(stock);
            mmPriceUtil.calcMaxMinIndex(120);
            stock2maxMinUtil.put(stock.getStockName(), mmPriceUtil);
        }

        double[] changePct;
        double[] advanceDeclineLine = new double[len];
        double[] newHighNewLow = new double[len];
        int marketCob;
        int availableStockCount, upCount, newHighCount, newLowCount;
        String stockName;
        for (int i = 0; i < len; i++) {
            marketCob = dates[i];
            availableStockCount = upCount = newHighCount = newLowCount = 0;
            for (int j = 0; j < tradableCnt; j++) {
                StockVo stock = tradableStocks.get(j);
                stockName = stock.getStockName();
                index = name2index.get(stockName);
                maxMinUtil = stock2maxMinUtil.get(stockName);

                if(stock.isSameDayWithIndex(marketCob, index)){
                    ++availableStockCount;
                    changePct = (double[])stock.queryCmpIndex(IndexChangePct.INDEX_NAME);

                    if(changePct[index] > 0d){
                        ++upCount;
                    }

                    if(maxMinUtil.getMaxIndex(index) == index){
                        ++newHighCount;
                    }

                    if(maxMinUtil.getMinIndex(index) == index){
                        ++newLowCount;
                    }

                    name2index.put(stockName, index + 1);       //advanceIndex
                }
            }

            if(availableStockCount > 0){
                advanceDeclineLine[i] = ((double)(upCount * 2 - availableStockCount)) / availableStockCount;
                newHighNewLow[i] = ((double)(newHighCount - newLowCount)) / availableStockCount;
            }
        }

        marketIndex.addIndex("advanceDeclineLine", maBase.calculate(advanceDeclineLine, 30));
        marketIndex.addIndex("newHighNewLow", newHighNewLow);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }
}
