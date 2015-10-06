package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<StockVo> stocks = stocksService.queryAllStock();

        IndexCalculator indexCalculator = new IndexCalculator(stocks, null);
        indexCalculator.calculate();

        stocksService.saveStocks(stocks);
		logger.info( description + " complete...");
	}

}
