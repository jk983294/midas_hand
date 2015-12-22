package com.victor.midas.calculator;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.correlation.VolumeCorr;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * aggregate all tradable stock info into more useful data
 */
public class AggregationCalculator {
    private static final Logger logger = Logger.getLogger(AggregationCalculator.class);

    private List<StockVo> stocks;
    private CalcParameter parameter;
    private StockVo indexSH;
    private List<StockVo> tradableStocks;
    private List<StockVo> indexStocks;

    /** aggregation calculators*/
    private VolumeCorr volumeCorr;

    public AggregationCalculator(List<StockVo> stocks) {
        this.stocks = stocks;
        StockFilterUtil filterUtil = new StockFilterUtil(stocks);
        filterUtil.filter();
        indexSH = filterUtil.getIndexSH();
        tradableStocks = filterUtil.getTradableStocks();
        indexStocks = filterUtil.getIndexStocks();
        parameter = new CalcParameter();
    }

    public void calculate() throws MidasException {
        logger.info("aggregation calculator start...");
        try {
//            volumeCorr = new VolumeCorr(stocks, indexSH, tradableStocks);
//            volumeCorr.calculate();
        } catch (Exception e){
            logger.error(e);
            throw new MidasException("problem meet when calculate aggregation index", e);
        }
        logger.info("aggregation calculator finish... ");
	}

    public CalcParameter getParameter() {
        return parameter;
    }

    public void setParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }

    public VolumeCorr getVolumeCorr() {
        return volumeCorr;
    }

    public StockVo getIndexSH() {
        return indexSH;
    }

    public List<StockVo> getTradableStocks() {
        return tradableStocks;
    }

    public List<StockVo> getIndexStocks() {
        return indexStocks;
    }
}
