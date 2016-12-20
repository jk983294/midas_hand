package com.victor.midas.calculator;

import java.util.List;

import com.victor.midas.calculator.common.ICalculator;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class IndexCalculator {
    private static final Logger logger = Logger.getLogger(IndexCalculator.class);

    private List<ICalculator> calculators;
    public ICalculator targetCalculator;
    public MidasTrainOptions options;

    private boolean isBigDataSet;
    private CalcParameter parameter;
    private StockFilterUtil filterUtil;


    public IndexCalculator(List<StockVo> stocks, String calcName) throws MidasException {
        this.parameter = IndexFactory.parameter;
        calculators = IndexFactory.getAllNeededCalculators(calcName);
        if(CollectionUtils.isNotEmpty(calculators) ){
            targetCalculator = calculators.get(calculators.size() - 1);
            options = targetCalculator.getTrainOptions();
        }
        isBigDataSet = stocks.size() > 100;
        filterUtil = new StockFilterUtil(stocks);
        filterUtil.filter();
        initCalculator();
    }

    public void calculate() throws MidasException {
        logger.info("calculation index start...");
        for (ICalculator calculator : calculators){
            calculate(calculator);
        }
        logger.info("calculation index finish... ");
	}

    private void calculate(ICalculator calculator) throws MidasException {
        if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Tradable){
            calculate(calculator, filterUtil.getTradableStocks());
        } else if(calculator.getCalculatorType() == MidasConstants.CalculatorType.All){
            // first calculate index then tradable, for some tradable may use index results
            calculate(calculator, filterUtil.getIndexStocks());
            calculate(calculator, filterUtil.getTradableStocks());
        } else if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Index){
            calculate(calculator, filterUtil.getIndexStocks());
        } else if(calculator.getCalculatorType() == MidasConstants.CalculatorType.Aggregation){
            calculateStock(calculator, null);
        }

    }

    private void calculate(ICalculator calculator, List<StockVo> stockCollection) throws MidasException {
        if(CollectionUtils.isNotEmpty(stockCollection)){
            for(StockVo stock : stockCollection){
                calculateStock(calculator, stock);
            }
        }
    }

    private void calculateStock(ICalculator calculator, StockVo stock) throws MidasException {
        try {
            if(stock == null) calculator.calculate();
            else calculator.calculate(stock);
        } catch (Exception e){
            logger.error(e);
            throw new MidasException(String.format("problem meet when calculate index %s for %s", calculator.getIndexName(), stock == null ? "aggregation" : stock.getStockName()), e);
        }
    }

    private void initCalculator(){
        for (ICalculator calculator : calculators){
            calculator.init_aggregation(filterUtil);
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
