package com.victor.midas.calculator.common;

import com.victor.midas.calculator.AggregationCalculator;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * interface for index calculation
 */
public abstract class IndexCalcBase {

    private static final Logger logger = Logger.getLogger(IndexCalcBase.class);

    public static final boolean useExistingData = false;

    protected CalcParameter parameter;

    protected StockVo stock;

    protected StockVo oldStock;

    protected Map<String, Object> cmpIndexName2Index;

    protected AggregationCalculator aggregationCalculator;

    protected Set<String> requiredCalculator = new LinkedHashSet<>();

    protected IndexCalcBase(CalcParameter parameter) {
        this.parameter = parameter;
    }
    protected IndexCalcBase() {}

    /**
     * one index name could map to several component indexes
     */
    public String getIndexNameOfStock(String stockName){
        return stockName +"_"+ getIndexName();
    }

    public abstract String getIndexName();

    public abstract void setRequiredCalculator();

    /**
     * no old result value to use, calculate it from scratch
     * try best to use existing array for training
     * return ( cmpName map to double[] or int[])
     */
    protected abstract void calculateFromScratch() throws MidasException;

    /**
     * use oldIndex value, reduce calculation overhead
     * return ( cmpName map to double[] or int[])
     */
    protected abstract void calculateFromExisting() throws MidasException;

    /**
     * get original index data structure, calculate in place
     */
    protected abstract void calculateForTrain() throws MidasException;

    /**
     * first init some data structure, then calculate, at last store result
     */
    public void calculate(StockVo stock, StockVo oldStock) throws MidasException {
        this.stock = stock;
        this.oldStock = oldStock;

        initIndex();

        if( !useExistingData || oldStock == null || !oldStock.isExistIndex(getIndexName())
                || oldStock.getStart() != stock.getStart()){
            calculateFromScratch();
            stock.addIndex(getIndexName(), cmpIndexName2Index);
        } else if(oldStock.getEnd() != stock.getEnd()){
            logger.info("calculateFromExisting for stock : " + stock.getStockName() + " index : " + getIndexName());
            calculateFromExisting();
            stock.addIndex(getIndexName(), cmpIndexName2Index);
        } else {
            logger.info("already calculate index for stock : " + stock.getStockName() + " index : " + getIndexName());
        }
    }

    public void calculate(StockVo stock) throws MidasException{
        calculate(stock, null);
    }

    /**
     * get original index data structure, calculate in place, no need to add index
     */
    public void calculateForTrainEntry(StockVo stock) throws MidasException {
        this.stock = stock;
        initIndexForTrain();
        calculateForTrain();
    }

    /**
     * init data structure for index
     */
    protected abstract void initIndex() throws MidasException;

    /**
     * get original index data structure
     */
    protected abstract void initIndexForTrain() throws MidasException;

    /**
     * for concrete calculator set their parameter
     */
    public abstract void applyParameter();

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

    public void setAggregationCalculator(AggregationCalculator aggregationCalculator) {
        this.aggregationCalculator = aggregationCalculator;
    }
}
