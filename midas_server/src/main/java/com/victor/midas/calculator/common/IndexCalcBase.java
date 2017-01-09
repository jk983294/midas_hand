package com.victor.midas.calculator.common;

import com.victor.midas.model.common.StockState;
import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreState;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.log4j.Logger;

import java.util.*;

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
    protected int itr;                              // iterator index

    protected PerfCollector perfCollector;
    protected StockState state;
    private StockScore _stockScore;

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
        changePct = stock.queryCmpIndexDoubleWithNull(MidasConstants.INDEX_NAME_CHANGE_PCT);
        dates = stock.getDatesInt();
        len = end.length;
        cmpIndexName2Index = new HashMap<>();
        state = StockState.HoldMoney;

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

    @Override
    public void setPerfCollector(PerfCollector perfCollector) {
        this.perfCollector = perfCollector;
    }

    @Override
    public PerfCollector getPerfCollector() {
        return this.perfCollector;
    }

    /**
     * default non-signal score strategy, buy next signal day's start, sell T + 1's end
     */
    protected void addStockScore(int signalCobIndex, double score){
        if(stock.getStockType() == StockType.Index) return;
        if(signalCobIndex + 1 < len){
            _stockScore = new StockScore(stock.getStockName(), score, dates[signalCobIndex]);
            _stockScore.buyIndex = signalCobIndex + 1;
            _stockScore.buyCob = dates[signalCobIndex + 1];
            _stockScore.buyTiming = 0;
            _stockScore.sellTiming = 1;

            if(signalCobIndex + 2 < len){
                _stockScore.sellIndex = signalCobIndex + 2;
                _stockScore.sellCob = dates[signalCobIndex + 2];
            } else {
                _stockScore.sellIndex = signalCobIndex + 1;
                _stockScore.sellCob = dates[signalCobIndex + 1];
            }

            _stockScore.holdingPeriod = _stockScore.sellIndex - _stockScore.buyIndex + 1;

            addScore2PerformanceCollector();
            _stockScore = null;
        }
    }

    public void setStateHoldStock(double score) {
        if(stock.getStockType() == StockType.Index) return;
        this.state = StockState.HoldStock;
        _stockScore = new StockScore(stock.getStockName(), score, dates[itr]);
        if(itr + 1 < len){
            _stockScore.buyIndex = itr + 1;
            _stockScore.buyCob = dates[itr + 1];
            _stockScore.buyTiming = 0;
            _stockScore.state = StockScoreState.Holding;
        }
    }

    /**
     * default is sell at sell signal day's close
     * when sell signal day is the buy day, then because the T + 1 policy, sell at next day's open
     * only when next day is unavailable, for calculation purpose, sell at last day's close even violate the T + 1 policy
     * @param isLastDayForceSell true, state won't change since it hasn't expire the strategy, false, state will change to Sold
     */
    public void setStateHoldMoney(boolean isLastDayForceSell) {
        if(stock.getStockType() == StockType.Index) return;
        this.state = StockState.HoldMoney;
        if(_stockScore != null){
            if(!isLastDayForceSell) {
                _stockScore.state = StockScoreState.Sold;
            }

            if(itr < len){
                _stockScore.sellIndex = itr;
                _stockScore.sellCob = dates[itr];
                _stockScore.sellTiming = 1;

                // T + 1, sell the T + 1 day open
                if(_stockScore.buyCob == _stockScore.sellCob && itr + 1 < len){
                    _stockScore.sellIndex = itr + 1;
                    _stockScore.sellCob = dates[itr + 1];
                    _stockScore.sellTiming = 0;
                }

                addScore2PerformanceCollector();
            } else if(len > 0){         // sell the last day's close
                _stockScore.sellIndex = len - 1;
                _stockScore.sellCob = dates[len - 1];
                _stockScore.sellTiming = 1;
                addScore2PerformanceCollector();
            }
        }
        _stockScore = null;
    }

    private void addScore2PerformanceCollector(){
        if(perfCollector.options.useSignal){
            perfCollector.cob2scoreList.get(_stockScore.getCob()).add(_stockScore);
        } else {
            perfCollector.cob2topK.get(_stockScore.getCob()).add(_stockScore);
        }
    }
}
