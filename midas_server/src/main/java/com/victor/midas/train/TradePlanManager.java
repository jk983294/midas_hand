package com.victor.midas.train;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.db.DayFocusDb;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.TrainItem;
import com.victor.midas.model.vo.TrainResult;
import com.victor.midas.train.strategy.common.FocusGenerator;
import com.victor.midas.train.strategy.common.StrategyFactory;
import com.victor.midas.train.strategy.common.TradeStrategy;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * used for train entry, for one time training and back test
 */
public class TradePlanManager {

    private static final Logger logger = Logger.getLogger(TradePlanManager.class);

    private String strategyName = "KLineStrategyS";

    private TradeStrategy strategy;

    private TrainResult trainResult;

    private List<DayFocusDb> focus;

    private boolean isBigDataSet;

    public TradePlanManager(CalcParameter parameter, List<StockVo> stocks, String strategyName) throws Exception {
        this.strategyName = strategyName;
        initStocks(stocks, parameter);
        strategy = StrategyFactory.getStrategyByName(this.strategyName, parameter, stocks);
        trainResult = new TrainResult(strategyName);
    }

    public void process() throws Exception {
        logger.info("start trade simulation ...");
        strategy.turnOnRecordHistory();
        strategy.tradeSimulation();
        CalcParameter parameterResult = (CalcParameter)strategy.getParameter().clone();
        strategy.performance();
        TrainItem item = strategy.getPortfolio().generateTrainResult();
        item.setParameter(parameterResult);
        trainResult.addResult(item);
        logger.info("start generate focus ...");
        FocusGenerator focusGenerator = new FocusGenerator(strategy.getTradableStocks(), strategy.getDatesSH());
        focusGenerator.execute();
        focus = focusGenerator.getFocus();
    }

    /**
     * in strategy, it use related calculator only, so it is needed to use all calculators to init
     */
    private void initStocks(List<StockVo> stocks, CalcParameter parameter) throws MidasException {
        IndexCalculator calculator = new IndexCalculator(stocks, strategyName);
        calculator.calculate();
        isBigDataSet = calculator.isBigDataSet();
        logger.info("init stock finished...");
    }

    public TrainResult getTrainResult() {
        return trainResult;
    }

    public void setTrainResult(TrainResult trainResult) {
        this.trainResult = trainResult;
    }

    public List<DayFocusDb> getFocus() {
        return focus;
    }

    public void setFocus(List<DayFocusDb> focus) {
        this.focus = focus;
    }

    public boolean isBigDataSet() {
        return isBigDataSet;
    }
}
