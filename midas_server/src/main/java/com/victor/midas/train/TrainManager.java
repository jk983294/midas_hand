package com.victor.midas.train;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.TrainItem;
import com.victor.midas.model.vo.TrainResult;
import com.victor.midas.train.common.StockGene;
import com.victor.midas.train.strategy.common.StrategyFactory;
import com.victor.midas.train.strategy.common.TradeStrategy;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.combination.Combinations;
import com.victor.utilities.math.opt.OptimizerBase;
import com.victor.utilities.math.opt.SimulatedAnnealing;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * used for train entry, for one time training and back test
 */
public class TrainManager {

    private static final Logger logger = Logger.getLogger(TrainManager.class);

    private String trainStrategyName = "KLineStrategyS";

    private TradeStrategy strategy;

    private TrainResult trainResult;

    private boolean isBigDataSet;

    private boolean needTrain;          // retrieved from concrete strategy

    public TrainManager(CalcParameter parameter, List<StockVo> stocks, String strategyName) throws Exception {
        this.trainStrategyName = strategyName;
        initStocks(stocks, parameter);
        strategy = StrategyFactory.getStrategyByName(trainStrategyName, parameter, stocks);
        needTrain = strategy.isNeedTrain();
        trainResult = new TrainResult(strategyName);
    }

    public void process() throws Exception {
        double[] lowbounds = strategy.getLowbounds();
        double[] upbounds = strategy.getUpbounds();

        List<int[]> discreteParams = strategy.getDiscreteParams();
        if(ArrayHelper.isNull(discreteParams)){
            if(needTrain){
                optimize(strategy.getInitParams(), lowbounds, upbounds);
            } else {
                simulation();
            }
        } else {
            // if have discrete parameters, iterate all combinations
            int[] choiceCnt = Combinations.getChoiceCnt(discreteParams);
            int[] choiceIndex = ArrayHelper.buildArray(choiceCnt.length, 0);
            int[] newParams = ArrayHelper.buildArray(choiceCnt.length, 0);
            do {
                newParams = Combinations.getChoice(discreteParams, choiceIndex, newParams);
                /**generate discrete parameter combination, then use discrete calculator to calculate those index */
                strategy.applyParameters(newParams);
                strategy.discreteCalculate();
                if(needTrain){
                    optimize(strategy.getInitParams(), lowbounds, upbounds);
                } else {
                    simulation();
                }

            } while (Combinations.allChoices(choiceIndex, choiceCnt));
        }
    }

    private void simulation() throws Exception {
        strategy.turnOnRecordHistory();
        strategy.tradeSimulation();
        recordTrainResult();
    }

    private void optimize(double[] initParams, double[] lowbounds, double[] upbounds) throws Exception {
        if(ArrayHelper.isNull(initParams) || ArrayHelper.isNull(lowbounds) || ArrayHelper.isNull(upbounds)) return;

        // set parameter
        strategy.applyParameters(initParams);
        StockGene stockOpter = new StockGene(initParams, strategy);
        OptimizerBase<StockGene> simulatedAnnealing = new SimulatedAnnealing<>(stockOpter, upbounds, lowbounds);
        simulatedAnnealing.train();

        // after optimize, record it
        StockGene best = simulatedAnnealing.getBest_params();
        best.simulation();
        recordTrainResult(best);
    }

    /**
     * record results, max optimizer
     */
    private void recordTrainResult(StockGene best) throws CloneNotSupportedException {
        CalcParameter parameterResult = (CalcParameter)strategy.getParameter().clone();
        TrainItem item = new TrainItem(parameterResult, strategy.getPortfolio().getHistory(), best.getFitness());
        trainResult.addResult(item);
        logger.info("best for this turn : " +best.toString());
    }

    private void recordTrainResult() throws Exception {
        CalcParameter parameterResult = (CalcParameter)strategy.getParameter().clone();
        strategy.performance();
        TrainItem item = strategy.getPortfolio().generateTrainResult();
        item.setParameter(parameterResult);
        trainResult.addResult(item);
    }

    /**
     * in strategy, it use related calculator only, so it is needed to use all calculators to init
     */
    private void initStocks(List<StockVo> stocks, CalcParameter parameter) throws MidasException {
        IndexCalculator calculator = new IndexCalculator(stocks, "train");
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

    public boolean isBigDataSet() {
        return isBigDataSet;
    }
}
