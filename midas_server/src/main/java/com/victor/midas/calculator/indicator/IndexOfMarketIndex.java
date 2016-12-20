package com.victor.midas.calculator.indicator;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasException;


/**
 * tradable stock intersection days of market index
 */
public class IndexOfMarketIndex extends IndexCalcBase {

    public final static String INDEX_NAME = "index2MarketIndex";
    public final static String INDEX_NAME1 = "marketIndex2Index";

    private int[] index2MarketIndex;
    private int[] marketIndex2Index;

    public IndexOfMarketIndex(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        if(!StockType.Index.equals(stock.getStockType())){
            int[] marketDates = filterUtil.getMarketIndex().getDatesInt();
            marketIndex2Index = new int[marketDates.length];
            stock.setCobIndex(0);
            int marketCob, cobIndex;
            for (int i = 0; i < marketDates.length; i++) {
                marketCob = marketDates[i];
                cobIndex = stock.getCobIndex();
                if(stock.isSameDayWithIndex(marketCob)){
                    index2MarketIndex[cobIndex] = i;
                    marketIndex2Index[i] = cobIndex;
                } else {
                    marketIndex2Index[i] = -1;
                }
                stock.advanceIndex(marketCob);
            }

            addIndexData(INDEX_NAME, index2MarketIndex);
//            addIndexData(INDEX_NAME1, marketIndex2Index);
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        index2MarketIndex = new int[len];
    }


}
