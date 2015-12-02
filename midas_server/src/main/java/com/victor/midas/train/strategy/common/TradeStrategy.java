package com.victor.midas.train.strategy.common;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.train.StockTrain;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.common.Portfolio;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * base class of trade strategy, concrete strategy should implement this abstract class
 * continuous parameter optimized by GA/SA in StockGene
 * discrete parameter optimized via iterate combination in trainManager
 */
public abstract class TradeStrategy {
    private static final Logger logger = Logger.getLogger(TradeStrategy.class);

    protected List<StockVo> allRawStocks;
    protected List<StockTrain> allStocks;

    protected List<StockTrain> tradableStocks;

    protected List<StockTrain> indexes;

    protected Map<String, StockTrain> stockMap;

    protected boolean needTrain;

    protected boolean isSmallSetData;

    /** benchmark date from SH index*/
    protected int[] datesSH;

    protected Portfolio portfolio;

    protected CalcParameter parameter;

    protected IndexCalculator calculator;

    protected double[] upbounds;
    protected double[] lowbounds;

    /** SH index correlation*/
    protected double[] stockIndexTotal;

    /** provide some calculators relate to discrete parameter iteration*/
    protected List<IndexCalcBase> discreteCalculators;
    /** provide some calculators relate to parameter mutation, used by trainManager to generate related indexes*/
    protected List<IndexCalcBase> continuousCalculators;

    protected TradeStrategy() {
    }

    protected TradeStrategy(List<StockVo> allStockVos, CalcParameter parameter, boolean needTrain) throws MidasException {
        this.parameter = parameter;
        this.needTrain = needTrain;
        this.allRawStocks = allStockVos;
        calculator = new IndexCalculator(allRawStocks, parameter);
        calculator.calculate();
        isSmallSetData = !calculator.isBigDataSet();
        portfolio = new Portfolio(parameter.getTradeTaxRate());
        try {
            initStocks(allStockVos);
            datesSH = stockMap.get(MidasConstants.SH_INDEX_NAME).getStock().getDatesInt();
            stockIndexTotal = calculator.getAggregationCalculator().getVolumeCorr().getAvgTotal();
        } catch (Exception e){
            logger.error("init trade strategy failed.");
            throw new MidasException("init trade strategy failed.", e);
        }
    }

    /** one time trading system simulation, generate performance */
    public abstract void tradeSimulation() throws Exception;

    public abstract void turnOnRecordHistory();

    protected void initSimulation(){
        portfolio.init();
        initStockTimeIndex();
    }

    /**
     * get tradeSimulation's performance, must run tradeSimulation first
     */
    public double performance() throws Exception {
        return portfolio.performance();
    }

    public abstract String getStrategyName();

    /** provide combinations for discrete parameter iteration*/
    public abstract List<int[]> getDiscreteParams();

    /**set those params into CalcParameter, then trainManager call related calculators */
    public abstract void applyParameters(double[] params);
    public abstract void applyParameters(int[] discreteParams);

    protected void initStockTimeIndex(){
        for(StockTrain stockTrain : allStocks){
            stockTrain.reSetCurrentIndex();
        }
    }

    /**
     * init data, split tradable stocks with index stocks
     */
    private void initStocks(List<StockVo> allStockVos) throws MidasException {
        tradableStocks = new ArrayList<>();
        indexes = new ArrayList<>();
        stockMap = new HashMap<>();
        allStocks = new ArrayList<>();
        for(StockVo stock : allStockVos){
            StockTrain stockTrain = new StockTrain(stock);
            stockTrain.initTrainDayIndex(parameter.getTrainStartDate(), parameter.getTrainEndDate(), parameter.getExceptionDays());
            if(stock.getStockType() == StockType.Index){
                indexes.add(stockTrain);
            } else {
                tradableStocks.add(stockTrain);
            }
            stockMap.put(stock.getStockName(), stockTrain);
            allStocks.add(stockTrain);
        }
        if(!isSmallSetData){
            logger.info("start init index stocks for large data set...");
            for(StockTrain stockTrain : indexes){
                initStockForAllIndex(stockTrain.getStock());
            }
        }
    }

    /**
     * for total stock set is too large, only calculate when needed
     */
    protected void initStockForAllIndex(StockVo toInitStock) throws MidasException {
        calculator.calculate(toInitStock);
    }

    /**
     * calculate discrete parameter controlled index
     */
    public void discreteCalculate() throws MidasException {
        if(!ArrayHelper.isNull(discreteCalculators)){
            IndexFactory.applyNewParameter(parameter, discreteCalculators);
            for(IndexCalcBase indexCalcBase : discreteCalculators){
                for(StockTrain stock : allStocks){
                    indexCalcBase.calculateForTrainEntry(stock.getStock());
                }
            }
        }
    }

    /**
     * calculate continuous parameter controlled index
     */
    public void continuousCalculate() throws MidasException {
        if(!ArrayHelper.isNull(continuousCalculators)){
            IndexFactory.applyNewParameter(parameter, continuousCalculators);
            for(IndexCalcBase indexCalcBase : continuousCalculators){
                for(StockTrain stock : allStocks){
                    indexCalcBase.calculateForTrainEntry(stock.getStock());
                }
            }
        }
    }

    public double[] getInitParams() {
        return MathHelper.randomRange(lowbounds, upbounds);
    }

    public double[] getUpbounds() {
        return upbounds;
    }

    public void setUpbounds(double[] upbounds) {
        this.upbounds = upbounds;
    }

    public double[] getLowbounds() {
        return lowbounds;
    }

    public void setLowbounds(double[] lowbounds) {
        this.lowbounds = lowbounds;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public int[] getDatesSH() {
        return datesSH;
    }

    public void setDatesSH(int[] datesSH) {
        this.datesSH = datesSH;
    }

    public CalcParameter getParameter() {
        return parameter;
    }

    public void setParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }

    public List<IndexCalcBase> getDiscreteCalculators() {
        return discreteCalculators;
    }

    public void setDiscreteCalculators(List<IndexCalcBase> discreteCalculators) {
        this.discreteCalculators = discreteCalculators;
    }

    public List<IndexCalcBase> getContinuousCalculators() {
        return continuousCalculators;
    }

    public void setContinuousCalculators(List<IndexCalcBase> continuousCalculators) {
        this.continuousCalculators = continuousCalculators;
    }

    public boolean isNeedTrain() {
        return needTrain;
    }

    public void setNeedTrain(boolean needTrain) {
        this.needTrain = needTrain;
    }

    public List<StockTrain> getTradableStocks() {
        return tradableStocks;
    }

    public void setTradableStocks(List<StockTrain> tradableStocks) {
        this.tradableStocks = tradableStocks;
    }

    public boolean isSmallSetData() {
        return isSmallSetData;
    }

    public void setSmallSetData(boolean isSmallSetData) {
        this.isSmallSetData = isSmallSetData;
    }
}
