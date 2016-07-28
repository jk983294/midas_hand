package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.stats.DayStatsAggregator;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class CalculateTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(CalculateTask.class);
	private static final String description = "Data Calculation Task";

	@Override
	public void doTask() throws Exception {
        List<StockVo> stocks = new ArrayList<>();
        IndexCalculator indexCalculator = null;
        if(params.size() == 2){         // calculate SH600598 score_revert
            StockVo stock = stocksService.queryStock(params.get(0));
            StockVo shStock = stocksService.queryStock(MidasConstants.MARKET_INDEX_NAME);
            stocks.add(stock);
            stocks.add(shStock);
            indexCalculator = new IndexCalculator(stocks, params.get(1));
        } else if(params.size() == 1){  // calculate score_revert
            stocks = stocksService.queryAllStock();
            indexCalculator = new IndexCalculator(stocks, params.get(0));
        } else {                        // calculate
            stocks = stocksService.queryAllStock();
            indexCalculator = new IndexCalculator(stocks, IndexChangePct.INDEX_NAME);
        }
        indexCalculator.calculate();
        saveResults(indexCalculator, stocks);
		logger.info( description + " complete...");
	}

    private void saveResults(IndexCalculator indexCalculator, List<StockVo> stocks) throws MidasException {
        if(indexCalculator.targetCalculator.getIndexName().equals(DayStatsAggregator.INDEX_NAME)){
            DayStatsAggregator aggregator = (DayStatsAggregator)indexCalculator.targetCalculator;
            stocksService.saveDayStatsList(aggregator.dayStatses);
        } else {
            if(params.size() == 2){
                stocksService.getStockDao().updateStock(stocks.get(0));
            } else {
                stocksService.saveStocks(stocks);
            }
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

}
