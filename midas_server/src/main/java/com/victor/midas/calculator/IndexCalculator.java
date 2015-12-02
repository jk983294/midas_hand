package com.victor.midas.calculator;

import java.util.List;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;

public class IndexCalculator {
    private static final Logger logger = Logger.getLogger(IndexCalculator.class);

    private List<IndexCalcBase> indexCalcBases;     // could be basic calculator when big data set
    private List<IndexCalcBase> indexCalcbasesAll;  // contain all index calculators
    private List<IndexCalcBase> indexCalcbasesForIndex;
    private List<IndexCalcBase> indexCalcbasesCommonForIndex;
    private AggregationCalculator aggregationCalculator;
    private boolean isBigDataSet;
    private List<StockVo> stocks;
    private CalcParameter parameter;


    public IndexCalculator(List<StockVo> stocks, CalcParameter parameter) throws MidasException {
        this.stocks = stocks;
        if(parameter == null){
            this.parameter = new CalcParameter();
        } else {
            this.parameter = parameter;
        }

        aggregationCalculator = new AggregationCalculator(stocks);
        indexCalcbasesAll = IndexFactory.getIndexCalcBases();
        indexCalcbasesForIndex = IndexFactory.getIndexCalcbasesForIndex();
        indexCalcbasesCommonForIndex = IndexFactory.getIndexCalcbasesCommonForIndex();
        if(stocks.size() < 100){
            indexCalcBases = indexCalcbasesAll;
            isBigDataSet = false;
        } else {
            indexCalcBases = IndexFactory.getIndexCalcbasesForBigDataSet();
            isBigDataSet = true;
        }

        // calculate aggregation results and Index level results
        calcAggregationIndex();
        calcForIndex();
        // use aggregation results and Index level results to init tradable stocks' calculators
        IndexFactory.applyNewParameter(this.parameter, indexCalcBases);
        IndexFactory.applyNewParameter(this.parameter, indexCalcbasesAll);
        IndexFactory.applyNewParameter(this.parameter, indexCalcbasesCommonForIndex);
        IndexFactory.setAggregationCalculator(indexCalcBases, aggregationCalculator);
        IndexFactory.setAggregationCalculator(indexCalcbasesAll, aggregationCalculator);
        IndexFactory.setAggregationCalculator(indexCalcbasesCommonForIndex, aggregationCalculator);
    }

    public void calculate() throws MidasException {
        logger.info("calculation use incremental mode : " + IndexCalcBase.useExistingData);
		for(StockVo stock : aggregationCalculator.getTradableStocks()){
            try {
                for (IndexCalcBase indexCalcBase : indexCalcBases){
                    indexCalcBase.calculate(stock);
                }
            } catch (Exception e){
                logger.error(e);
                throw new MidasException("problem meet when calculate index for " + stock, e);
            }
        }
        logger.info("calculation index finish... ");
	}

    /**
     * when calculate for one stock, use all calculator
     * @param stockVo
     * @throws MidasException
     */
    public void calculate(StockVo stockVo) throws MidasException {
        try {
            for (IndexCalcBase indexCalcBase : indexCalcbasesAll){
                indexCalcBase.calculate(stockVo);
            }
        } catch (Exception e){
            logger.error(e);
            throw new MidasException("problem meet when calculate index for " + stockVo, e);
        }
    }

    private void calcAggregationIndex() throws MidasException {
        aggregationCalculator.calculate();
    }

    private void calcForIndex() throws MidasException {
        logger.info("calculation for Index ");
        for(StockVo stock : aggregationCalculator.getIndexStocks()){
            try {
                for (IndexCalcBase indexCalcBase : indexCalcbasesCommonForIndex){
                    indexCalcBase.calculate(stock);
                }
                for (IndexCalcBase indexCalcBase : indexCalcbasesForIndex){
                    indexCalcBase.calculate(stock);
                }
            } catch (Exception e){
                logger.error(e);
                throw new MidasException("problem meet when calculate index for " + stock, e);
            }
        }
        logger.info("calculation for Index finish... ");
    }

    public CalcParameter getParameter() {
        return parameter;
    }

    public void setParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }

    public boolean isBigDataSet() {
        return isBigDataSet;
    }

    public AggregationCalculator getAggregationCalculator() {
        return aggregationCalculator;
    }

    public void setAggregationCalculator(AggregationCalculator aggregationCalculator) {
        this.aggregationCalculator = aggregationCalculator;
    }
}
