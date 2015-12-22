package com.victor.midas.calculator.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * interface for index calculation
 */
public abstract class IndexCalcBase implements ICalculator {

    private static final Logger logger = Logger.getLogger(IndexCalcBase.class);

    protected CalcParameter parameter;

    protected StockVo stock;

    protected Map<String, Object> cmpIndexName2Index;

    protected Set<String> requiredCalculators = new LinkedHashSet<>();
    // for some index needs aggregation index, then use this to reference SH index
    protected StockFilterUtil filterUtil;

    protected IndexCalcBase(CalcParameter parameter) {
        this.parameter = parameter;
        setRequiredCalculators();
    }

    /**
     * one index name could map to several component indexes
     */
    public String getIndexNameOfStock(String stockName){
        return stockName +"_"+ getIndexName();
    }

    /**
     * first init some data structure, then calculate, at last store result
     */
    public void calculate(StockVo stock) throws MidasException {
        this.stock = stock;

        initIndex();
        calculate();
        stock.addIndex(getIndexName(), cmpIndexName2Index);
    }

    /**
     * init data structure for index
     */
    protected abstract void initIndex() throws MidasException;

    @Override
    public void init_aggregation(StockFilterUtil filterUtil){
        this.filterUtil = filterUtil;
    }

    @Override
    public MidasConstants.CalculatorType getCalculatorType() {
        return MidasConstants.CalculatorType.All;
    }

//    protected Map<String, Object> generateCmpName2IndexData(String cmpName, double[] indexData){
//        Map<String, Object> map = new HashMap<>();
//        map.put(cmpName, indexData);
//        return map;
//    }
//
//    protected Map<String, Object> generateCmpName2IndexData(String cmpName, int[] indexData){
//        Map<String, Object> map = new HashMap<>();
//        map.put(cmpName, indexData);
//        return map;
//    }

    protected void addIndexData(String cmpName, int[] indexData){
        cmpIndexName2Index.put(cmpName, indexData);
    }

    protected void addIndexData(String cmpName, double[] indexData){
        cmpIndexName2Index.put(cmpName, indexData);
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
    public Set<String> getRequiredCalculators() {
        return requiredCalculators;
    }

    @Override
    public void setRequiredCalculators() {
    }
}
