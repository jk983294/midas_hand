package com.victor.midas.calculator.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * interface for index calculation
 */
public abstract class AggregationCalcBase implements ICalculator {

    private static final Logger logger = Logger.getLogger(AggregationCalcBase.class);

    protected Set<String> requiredCalculators = new LinkedHashSet<>();
    protected List<StockVo> stocks;
    protected CalcParameter parameter;
    protected StockVo indexSH;
    protected List<StockVo> tradableStocks;
    protected int tradableCnt;
    protected Map<String, Integer> name2index;    // stock name map to date index
    protected int[] dates;                        // benchmark time line
    protected int index;                          // benchmark stock's date index
    protected int len;                            // benchmark stock's date len


    protected AggregationCalcBase(CalcParameter parameter) {
        this.parameter = parameter;
        setRequiredCalculators();
    }

    @Override
    public MidasConstants.CalculatorType getCalculatorType() {
        return MidasConstants.CalculatorType.Aggregation;
    }

    @Override
    public void init_aggregation(StockFilterUtil filterUtil){
        if(filterUtil != null){
            this.stocks = filterUtil.getAllStockVos();
            this.indexSH = filterUtil.getIndexSH();
            this.tradableStocks = filterUtil.getTradableStocks();
            dates = indexSH.getDatesInt();
            name2index = new HashMap<>();
            for(StockVo stock : tradableStocks){
                name2index.put(stock.getStockName(), 0);
            }
            len = dates.length;
            tradableCnt = tradableStocks.size();
            index = 0;
        }
    }

    @Override
    public void calculate(StockVo stock) throws MidasException {
        calculate();
    }

    @Override
    public Set<String> getRequiredCalculators() {
        return requiredCalculators;
    }

    public CalcParameter getParameter() {
        return parameter;
    }

    public void setParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void applyParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void setRequiredCalculators() {
        // do nothing
    }
}
