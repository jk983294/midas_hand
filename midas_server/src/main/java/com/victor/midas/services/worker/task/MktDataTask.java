package com.victor.midas.services.worker.task;

import java.util.List;

import com.victor.midas.model.common.MarketDataType;
import com.victor.midas.model.vo.MidasBond;
import com.victor.midas.services.worker.loader.*;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MktDataTask extends TaskBase {
	
	private static final Logger logger = Logger.getLogger(MktDataTask.class);
	private static final String description = "Market Data Load Task";

    private MarketDataType dataType = MarketDataType.stock;

    @Override
	public void doTask() throws Exception {
        dataType = MarketDataType.getDataType(MarketDataType.stock, params, 0);
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
            case tushare_report: {
                IDataLoader dataLoader = new FundamentalDataLoader();
                List<StockVo> fundmentals = (List<StockVo>)dataLoader.load(environment.getProperty("MktDataLoader.Tushare.stock.report"));
                break;
            }
        }
		logger.info( description + " complete...");
	}

    @Override
    public String getDescription() {
        return description;
    }

}
