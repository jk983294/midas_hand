package com.victor.midas.calculator.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
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
    // for some index needs aggregation index, then use this to reference market index
    protected StockFilterUtil filterUtil;

    protected double singleDouble = 1d;
    protected int singleInt = 1;

    protected int[] dates;
    protected double[] end, start, max, min, volume, total, changePct;
    protected int len;

    protected IndexCalcBase(CalcParameter parameter) {
        this.parameter = parameter;
        setRequiredCalculators();
    }

    /**
     * first init some data structure, then calculate, at last store result
     */
    public void calculate(StockVo stock) throws MidasException {
        this.stock = stock;
        _initIndex();
        calculate();
        stock.addIndex(getIndexName(), cmpIndexName2Index);
    }

    private void _initIndex() throws MidasException{
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        volume = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_VOLUME);
        changePct = stock.queryCmpIndexDoubleWithNull(MidasConstants.INDEX_NAME_CHANGEPCT);
        dates = stock.getDatesInt();
        len = end.length;
        cmpIndexName2Index = new HashMap<>();

        initIndex();
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

    @Override
    public MidasTrainOptions getTrainOptions() {
        MidasTrainOptions options = new MidasTrainOptions();
        return options;
    }
}
