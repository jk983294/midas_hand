package com.victor.midas.calculator;

import java.util.ArrayList;
import java.util.List;

import com.victor.midas.calculator.common.ICalculator;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class IndexCalculator {
    private static final Logger logger = Logger.getLogger(IndexCalculator.class);

    private List<ICalculator> calculators;

    private boolean isBigDataSet;
    private List<StockVo> stocks;
    private CalcParameter parameter;
    private StockFilterUtil filterUtil;
    private int calculatorCnt, lastAggregationIndex = -1;


    public IndexCalculator(List<StockVo> stocks, String calcName) throws MidasException {
        ctor(stocks, calcName);
    }

    public IndexCalculator(StockVo stock, String calcName) throws MidasException {
        List<StockVo> stocks = new ArrayList<>();
        stocks.add(stock);
        ctor(stocks, calcName);
    }

    private void ctor(List<StockVo> stocks, String calcName) throws MidasException {
        this.stocks = stocks;
        this.parameter = IndexFactory.parameter;
        calculators = IndexFactory.getAllNeededCalculators(calcName);
        isBigDataSet = stocks.size() > 100;
        filterUtil = new StockFilterUtil(this.stocks);
        filterUtil.filter();
        initCalculator();
    }

    public void calculate() throws MidasException {
        logger.info("calculation index start...");
        if(isBigDataSet && lastAggregationIndex >= 0){
            for(int i = 0; i <= lastAggregationIndex; i++){
                calculate(calculators.get(i));
            }
        } else {
            for (ICalculator calculator : calculators){
                calculate(calculator);
            }
        }
        logger.info("calculation index finish... ");
	}

    private void calculate(ICalculator calculator) throws MidasException {
        List<StockVo> stockCollection = null;
        if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Tradable){
            stockCollection = filterUtil.getTradableStocks();
        } else if(calculator.getCalculatorType() == MidasConstants.CalculatorType.All){
            stockCollection = filterUtil.getAllStockVos();
        } else if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Index){
            stockCollection = filterUtil.getIndexStocks();
        } else if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Aggregation){
            try {
                calculator.calculate();
                return;
            } catch (Exception e){
                logger.error(e);
                throw new MidasException(String.format("problem meet when calculate aggregation index %s", calculator.getIndexName()), e);
            }
        }
        if(CollectionUtils.isNotEmpty(stockCollection)){
            for(StockVo stock : stockCollection){
                try {
                    calculator.calculate(stock);
                } catch (Exception e){
                    logger.error(e);
                    throw new MidasException(String.format("problem meet when calculate tradable index %s for %s", calculator.getIndexName(), stock.getStockName()), e);
                }
            }
        }

    }

    private void initCalculator(){
        calculatorCnt = calculators.size();
        for(int i = 0; i < calculatorCnt; i++){
            ICalculator calculator = calculators.get(i);
            calculator.init_aggregation(filterUtil);
            if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Aggregation){
                lastAggregationIndex = i;
            }
        }
    }

    /**
     * when calculate for one stock, use all calculator after last aggregation calculator
     * @param stockVo
     * @throws MidasException
     */
    public void calculate(StockVo stockVo) throws MidasException {
        try {
            for(int i = lastAggregationIndex + 1; i < calculatorCnt; i++){
                ICalculator calculator = calculators.get(i);
                calculator.calculate(stockVo);
            }
        } catch (Exception e){
            logger.error(e);
            throw new MidasException("problem meet when calculate index for " + stockVo, e);
        }
    }

    public void apply(CalcParameter parameter) throws MidasException {
        this.parameter = parameter;
        IndexFactory.applyNewParameter(parameter, calculators);
        calculate();
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

    public void setBigDataSet(boolean isBigDataSet) {
        this.isBigDataSet = isBigDataSet;
    }

    public StockFilterUtil getFilterUtil() {
        return filterUtil;
    }
}
