package com.victor.midas.calculator.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * interface for index calculation
 */
public abstract class AggregationCalcbase {

    private static final Logger logger = Logger.getLogger(AggregationCalcbase.class);

    protected List<StockVo> stocks;
    protected CalcParameter parameter;
    protected StockVo indexSH;
    protected List<StockVo> tradableStocks;
    protected int tradableCnt;
    protected Map<String, Integer> name2index;    // stock name map to date index
    protected int[] dates;                        // benchmark time line
    protected int index;                          // benchmark stock's date index
    protected int len;                            // benchmark stock's date len


    protected AggregationCalcbase(List<StockVo> stocks, StockVo indexSH, List<StockVo> tradableStocks) {
        this.stocks = stocks;
        this.indexSH = indexSH;
        this.tradableStocks = tradableStocks;
        parameter = new CalcParameter();
        name2index = new HashMap<>();
        dates = indexSH.getDatesInt();
        for(StockVo stock : tradableStocks){
            name2index.put(stock.getStockName(), 0);
        }
        len = dates.length;
        tradableCnt = tradableStocks.size();
        index = 0;
    }

    public abstract void calculate() throws MidasException;

    /**
     * for concrete calculator set their parameter
     */
    public abstract void applyParameter();

    public CalcParameter getParameter() {
        return parameter;
    }

    public void setParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }
}
