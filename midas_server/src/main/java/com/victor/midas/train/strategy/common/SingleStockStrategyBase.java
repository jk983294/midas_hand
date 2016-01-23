package com.victor.midas.train.strategy.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.train.StockDecision;
import com.victor.midas.model.train.StockTrain;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.ModelConvertor;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * base class of trade strategy, concrete strategy should implement this abstract class
 * continuous parameter optimized by GA/SA in StockGene
 * discrete parameter optimized via iterate combination in trainManager
 */
public abstract class SingleStockStrategyBase extends TradeStrategy {

    private static final Logger logger = Logger.getLogger(SingleStockStrategyBase.class);

    /** current simulation stock, its portfolioItem, start point, end point*/
    protected StockTrain stock;
    private String stockCode;

    protected int startIndex = 0;
    protected int endIndex = 0;
    protected int shStockIndex;

    protected double[] end;
    protected double[] start;
    protected double[] max;
    protected double[] min;
    protected double[] total;
    protected int[] dates;

    protected StockDecision decision, buyReason;

    protected int[] signals;

    protected SingleStockStrategyBase(List<StockVo> allStockVos, CalcParameter parameter, boolean needTrain) throws MidasException {
        super(allStockVos, parameter, needTrain);
    }

    /** one time trading system simulation for single stock*/
    public void tradeSimulationPerStock() throws MidasException{
        if(endIndex > startIndex && endIndex < dates.length){
            decision = StockDecision.WATCH;
            int startDate = dates[startIndex], endDate = dates[endIndex], shDate, ShLen;
            int currentStockIndex = startIndex;
            // go through time line
            for (shStockIndex = 0, ShLen = datesSH.length; shStockIndex < ShLen; shStockIndex++) {
                shDate = datesSH[shStockIndex];
                if(shDate >= startDate && shDate <= endDate && shDate == dates[currentStockIndex]){
                    stock.setCurrentIndex(currentStockIndex);       // important!!!

                    //could buy at current day due to yesterday's decision
                    if(StockDecision.isWillBuyDecision(decision)){
                        checkCouldBuyToday(currentStockIndex);
                    } else if(decision == StockDecision.HOLD){    // check if it is sell point
                        maintainDataWhenHolding(currentStockIndex);
                        decision = checkIfSell(currentStockIndex);
                    } else {
                        checkCouldBuyTomorrow(currentStockIndex);
                    }

                    signals[currentStockIndex] = decision.ordinal();

                    // take action according to decision make
                    if(StockDecision.isBuyDecision(decision)){
                        buy(currentStockIndex);
                        decision = StockDecision.HOLD;
                    } else if(StockDecision.isSellDecision(decision)){
                        sell(currentStockIndex);
                        decision = StockDecision.WATCH;
                    }

                    ++currentStockIndex;
                }
            }
        }
        // record into stock, then save by train task
        stock.getStock().addIndex("prod", signals);
    }

    private void buy(int index){
        if(decision == StockDecision.BUY_AT_START){
            portfolio.buy(stockCode, stock.getCurrentDate(), start[index], buyReason);
        } else if(decision == StockDecision.BUY_AT_END){
            portfolio.buy(stockCode, stock.getCurrentDate(), end[index], buyReason);
        }
    }

    private void sell(int index){
        portfolio.sell(stockCode, stock.getCurrentDate(), end[index], decision);
    }

    /**
     * check if could buy today, because yesterday decision says it is considerable to buy today
     * it should give buy reason and buy timing
     * it should also record data for its stop loss strategy
     **/
    public abstract void checkCouldBuyToday(int index) throws MidasException;
    /*** check if could sell today **/
    public abstract StockDecision checkIfSell(int index);
    /*** update some data when holding stock, those data could be used to stop loss **/
    public abstract void maintainDataWhenHolding(int index);
    /*** check if could sell today **/
    public abstract boolean checkCouldBuyTomorrow(int index);
    /** extract data from single stock, prepare for simulation */
    public abstract void initStrategySpecifiedData() throws MidasException;


    /** one time trading system simulation, generate dayPerformance */
    @Override
    public void tradeSimulation() throws Exception{
        portfolio.init();
        // iterate one stock each individually
        for (int j = 0; j < tradableStocks.size(); j++) {
            portfolio.init();
            stock = tradableStocks.get(j);
            if(!isSmallSetData){
                initStockForAllIndex(stock.getStock());
            }

            initCommonData();
            initStrategySpecifiedData();

            try{
                tradeSimulationPerStock();
            } catch (Exception e){
                logger.error("problem meet with stock : " + stockCode + " exception : " + e.toString());
                throw new MidasException("problem meet with stock : " + stockCode, e);
            }

            if(!isSmallSetData){
                ModelConvertor.removeUnnecessaryIndex(stock);
            }
        }

        portfolio.generateRecord(stockMap);
    }

    @Override
    public void turnOnRecordHistory(){
        portfolio.setHistoryRecord(true);
    }

    protected void initCommonData() throws MidasException {
        stockCode = stock.getStock().getStockName();
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        dates = stock.getDates();

        startIndex = stock.getStartIndex();
        endIndex = stock.getEndIndex();

        /** init data */
        signals = new int[end.length];
    }
}
