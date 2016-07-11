package com.victor.midas.services.worker.task;

import java.util.List;

import com.victor.midas.model.common.MarketDataType;
import com.victor.midas.model.vo.MidasBond;
import com.victor.midas.services.worker.loader.FundDataLoader;
import com.victor.midas.services.worker.loader.IDataLoader;
import com.victor.midas.services.worker.loader.NationalDebtDataLoader;
import com.victor.midas.services.worker.loader.StockDataLoader;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.victor.midas.dao.TaskDao;
import org.springframework.core.env.Environment;

public class MktDataTask extends TaskBase {
	
	private static final Logger logger = Logger.getLogger(MktDataTask.class);
	private static final String description = "Market Data Load Task";

	private StocksService stocksService;
    private Environment environment;
    private MarketDataType dataType = MarketDataType.stock;

	public MktDataTask(TaskDao taskdao, StocksService stocksService, Environment environment, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
        this.environment = environment;
        dataType = MarketDataType.getDataType(MarketDataType.stock, params, 0);
	}

    public MktDataTask() {
    }

    @Override
	public void doTask() throws Exception {
        switch (dataType){
            case stock: {
                IDataLoader dataLoader = new StockDataLoader();
                List<StockVo> stocks = (List<StockVo>)dataLoader.load(environment.getProperty("MktDataLoader.Stock.Path"));
                if(CollectionUtils.isNotEmpty(stocks)){
                    stocksService.saveStocks(stocks);
                }
                break;
            }
            case fund: {
                IDataLoader dataLoader = new FundDataLoader();
                List<StockVo> stocks = (List<StockVo>)dataLoader.load(environment.getProperty("MktDataLoader.Fund.Path"));
                if(CollectionUtils.isNotEmpty(stocks)){
                    stocksService.saveStocks(stocks);
                }
                break;
            }
            case bond: {
                IDataLoader dataLoader = new NationalDebtDataLoader();
                List<MidasBond> bonds = (List<MidasBond>)dataLoader.load(environment.getProperty("MktDataLoader.Bond.national.debt"));
                if(CollectionUtils.isNotEmpty(bonds)){
                    stocksService.saveNationalDebt(bonds);
                }
                break;
            }
        }
		logger.info( description + " complete...");
	}

}
