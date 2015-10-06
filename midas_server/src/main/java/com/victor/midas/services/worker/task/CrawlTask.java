package com.victor.midas.services.worker.task;

import com.victor.midas.crawl.ConceptCrawler;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import org.apache.log4j.Logger;

import java.util.List;

public class CrawlTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(CrawlTask.class);
	private static final String description = "Data Crawl Task";

	private StocksService stocksService;


	public CrawlTask(TaskDao taskdao, StocksService stocksService, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
	}

	@Override
	public void doTask() throws Exception {
        List<String> stockNames = stocksService.queryAllStockNames();

        ConceptCrawler crawler = new ConceptCrawler(stockNames);
        crawler.crawl();

        stocksService.saveAllStockCrawlData(crawler.getCrawlDatas());
		logger.info( description + " complete...");
	}

}
