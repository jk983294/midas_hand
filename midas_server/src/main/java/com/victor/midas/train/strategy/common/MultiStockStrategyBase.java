package com.victor.midas.train.strategy.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.train.StockTrain;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.common.Portfolio;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * base class of trade strategy, concrete strategy should implement this abstract class
 * continuous parameter optimized by GA/SA in StockGene
 * discrete parameter optimized via iterate combination in trainManager
 */
public abstract class MultiStockStrategyBase extends TradeStrategy {
    private static final Logger logger = Logger.getLogger(MultiStockStrategyBase.class);

    protected Portfolio portfolio;

    protected MultiStockStrategyBase() {
    }

    protected MultiStockStrategyBase(List<StockVo> allStockVos, CalcParameter parameter, boolean needTrain) throws MidasException {
        super(allStockVos, parameter, needTrain);
    }

    /** one time trading system simulation, generate performance */
    public abstract void tradeSimulation() throws Exception;

    public abstract double tradePerformance();

    public abstract void turnOnRecordHistory();

    protected void initSimulation(){
        portfolio.init();
        initStockTimeIndex();
    }

    public double performance() throws Exception {
        initSimulation();
        tradeSimulation();
        return tradePerformance();
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
    }
}
