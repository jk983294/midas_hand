package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasConstants;
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
        if(params.size() == 2){         // calculate SH600598 score_revert
            StockVo stock = stocksService.queryStock(params.get(0));
            StockVo shStock = stocksService.queryStock(MidasConstants.MARKET_INDEX_NAME);
            List<StockVo> stockVos = new ArrayList<>();
            stockVos.add(stock);
            stockVos.add(shStock);
            IndexCalculator indexCalculator = new IndexCalculator(stockVos, params.get(1));
            indexCalculator.calculate();
            stocksService.getStockDao().updateStock(stock);
        } else if(params.size() == 1){  // calculate score_revert
            List<StockVo> stocks = stocksService.queryAllStock();
            IndexCalculator indexCalculator = new IndexCalculator(stocks, params.get(0));
            indexCalculator.calculate();
            stocksService.saveStocks(stocks);
        } else {                        // calculate
            List<StockVo> stocks = stocksService.queryAllStock();
            IndexCalculator indexCalculator = new IndexCalculator(stocks, IndexChangePct.INDEX_NAME);
            indexCalculator.calculate();
            stocksService.saveStocks(stocks);
        }
		logger.info( description + " complete...");
	}

    @Override
    public String getDescription() {
        return description;
    }

}
