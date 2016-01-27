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

    protected double singleDouble = 1d;
    protected int singleInt = 1;

    protected IndexCalcBase(CalcParameter parameter) {
        this.parameter = parameter;
        setRequiredCalculators();
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

    protected void addIndexData(String cmpName, int[] indexData){
        if(indexData == null) return;
        cmpIndexName2Index.put(cmpName, indexData);
    }

    protected void addIndexData(String cmpName, double[] indexData){
        if(indexData == null) return;
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
        this.singleDouble = parameter.singleDouble;
        this.singleInt = parameter.singleInt;
    }

    @Override
    public Set<String> getRequiredCalculators() {
        return requiredCalculators;
    }

    @Override
    public void setRequiredCalculators() {
    }
}
