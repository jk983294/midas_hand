package com.victor.midas.services.worker.task;

import com.victor.midas.crawl.ConceptCrawler;
import com.victor.midas.crawl.GuBaPageProcessor;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.spider.core.Spider;
import com.victor.spider.core.pipeline.JsonFilePipeline;
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
        logger.info( description + " start...");
        List<String> stockNames = stocksService.queryAllStockNames();
        crawlConcept(stockNames);
        CmdParameter cmdParameter = CmdParameter.guba;
        if(params.size() > 0){
            cmdParameter = CmdParameter.valueOf(params.get(0));
        }
        switch(cmdParameter){
            case guba :  crawlGuBa(stockNames);  break;
            case concept : crawlConcept(stockNames);  break;
            default : logger.error("no such parameter in crawl task.");
        }

		logger.info( description + " complete...");
	}

    public void crawlConcept(List<String> stockNames){
        ConceptCrawler crawler = new ConceptCrawler(stockNames);
        crawler.crawl();
        stocksService.saveAllStockCrawlConceptData(crawler.getCrawlDatas());
    }

    public void crawlGuBa(List<String> stockNames){
        String stockCode = "000702";
        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,601766,211862489_1.html")
                .addPipeline(new JsonFilePipeline("D:\\MktData\\guba")).run();
    }

}
