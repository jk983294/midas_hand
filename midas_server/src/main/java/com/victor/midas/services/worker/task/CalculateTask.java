package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import org.apache.log4j.Logger;

import java.util.List;

public class CalculateTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(CalculateTask.class);
	private static final String description = "Data Calculation Task";

	private StocksService stocksService;


	public CalculateTask( TaskDao taskdao, StocksService stocksService, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
	}

	@Override
	public void doTask() throws Exception {
        if(params.size() == 2){
            StockVo stock = stocksService.queryStock(params.get(0));
            IndexCalculator indexCalculator = new IndexCalculator(stock, params.get(1));
            indexCalculator.calculate();
            stocksService.getStockDao().updateStock(stock);
        } else if(params.size() == 1){
            List<StockVo> stocks = stocksService.queryAllStock();
            IndexCalculator indexCalculator = new IndexCalculator(stocks, params.get(0));
            indexCalculator.calculate();
            stocksService.saveStocks(stocks);
        } else {
            List<StockVo> stocks = stocksService.queryAllStock();
            IndexCalculator indexCalculator = new IndexCalculator(stocks, IndexChangePct.INDEX_NAME);
            indexCalculator.calculate();
            stocksService.saveStocks(stocks);
        }
		logger.info( description + " complete...");
	}

}
