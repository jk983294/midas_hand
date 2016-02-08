package com.victor.midas.calculator.indicator;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate change percentage per day
 */
public class IndexOfMarketIndex extends IndexCalcBase {

    public final static String INDEX_NAME = "indexOfMarketIndex";

    private int[] indexOfMarketIndex;

    private double[] end;

    private int len;

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
            int[] dates = filterUtil.getIndexSH().getDatesInt();
            stock.setCobIndex(0);
            boolean isNotSameDay = false;
            int cob, cobIndex;
            for (int i = 0; i < dates.length; i++) {
                cob = dates[i];
                cobIndex = stock.getCobIndex();
                if(!stock.isSameDayWithIndex(cob)){
                    if(!isNotSameDay) isNotSameDay = true;
                } else {
                    if(isNotSameDay){
                    }
                    isNotSameDay = false;
                }
                stock.advanceIndex(cob);
            }

            addIndexData(INDEX_NAME, indexOfMarketIndex);
        }

    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        indexOfMarketIndex = new int[len];
        cmpIndexName2Index = new HashMap<>();
    }


}
