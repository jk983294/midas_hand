package com.victor.midas.train.common;

import com.victor.midas.train.strategy.common.TradeStrategy;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.opt.Gene;

/**
 * stock strategy optimize gene
 */
public class StockGene extends Gene {

    private TradeStrategy strategy;

    public StockGene(double[] param, TradeStrategy strategy) {
        super(param);
        this.strategy = strategy;
    }

    @Override
    public void objective() throws Exception {
        // generate new parameter, then use those parameter to calculate index, then use those data to run a trade simulation
        strategy.applyParameters(param);
        strategy.continuousCalculate();
        strategy.tradeSimulation();
        fitness = strategy.performance();
    }

    /**
     * used for record history after optimization
     */
    public void simulation() throws Exception {
        strategy.turnOnRecordHistory();
        strategy.applyParameters(param);
        strategy.continuousCalculate();
        strategy.tradeSimulation();
        fitness = strategy.performance();
    }

    public TradeStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(TradeStrategy strategy) {
        this.strategy = strategy;
    }
}
