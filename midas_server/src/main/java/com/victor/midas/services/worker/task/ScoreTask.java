package com.victor.midas.services.worker.task;

import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.services.worker.loader.FundDataLoader;
import com.victor.midas.services.worker.loader.StockDataLoader;
import com.victor.midas.train.score.ConceptScoreManager;
import com.victor.midas.train.score.GeneralScoreManager;
import com.victor.midas.train.score.ScoreManager;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.PerformanceUtil;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class ScoreTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(ScoreTask.class);
	private static final String description = "Score Task";

    /** default come from DB*/
    private boolean isFromFileSystem = false;
    private boolean loadStock = true;   // false means load fund
    private String loadPath;

    public void init(boolean isFromFileSystem, boolean loadStock) {
        this.isFromFileSystem = isFromFileSystem;
        loadPath = environment.getProperty("MktDataLoader.Stock.Path");
        this.loadStock = loadStock;
        if(!loadStock){
            loadPath = environment.getProperty("MktDataLoader.Fund.Path");
        }
    }

    private List<StockVo> getAllStock() throws Exception {
        if(isFromFileSystem){
            if(loadStock){
                return (List<StockVo>)(new StockDataLoader().load(loadPath));
            } else {
                return (List<StockVo>)(new FundDataLoader().load(loadPath));
            }
        } else {
            if(params != null && params.size() > 1) {
                StockVo stock = stocksService.queryStock(params.get(1));
                StockVo shStock = stocksService.queryStock(MidasConstants.MARKET_INDEX_NAME);
                List<StockVo> stockVos = new ArrayList<>();
                stockVos.add(stock);
                stockVos.add(shStock);
                return stockVos;
            }
            return stocksService.queryAllStock();
        }
    }

	@Override
	public void doTask() throws Exception {
        CmdParameter cmdParameter = CmdParameter.getParameter(CmdParameter.score_ma, params, 0);

        ScoreManager manager = getScoreManager(cmdParameter);

        logger.info( "start score ...");
        manager.process();
        saveResults(manager);

        PerformanceUtil.manuallyGC(manager.getStocks());

		logger.info( description + " complete...");
	}

    private void saveResults(ScoreManager manager) throws MidasException {
        scoreDao.save(manager.getScoreRecords());
        if(isFromFileSystem || !manager.isBigDataSet()){
            logger.info("start save stocks ...");
            stocksService.saveStocks(manager.getStocks());               // maybe train strategy has generate new data
        }
    }

    private ScoreManager getScoreManager(CmdParameter cmdParameter) throws Exception {
        List<StockVo> stocks = getAllStock();
        String indexName = CmdParameter.getIndexName(cmdParameter);
        switch(cmdParameter){
            case score_ma:
            case score_support:
            case score_macd:
            case score_pcrs:
            case score_tfs:
            case score_revert: return new GeneralScoreManager(stocks, indexName);
            case score_concept: {
                List<StockCrawlData> crawlData = stocksService.queryAllStockCrawlData();
                return new ConceptScoreManager(stocks, indexName, crawlData);
            }
            default : logger.error("no such ScoreManager in score task.");
        }
        return new GeneralScoreManager(stocks, indexName);
    }

    @Override
    public String getDescription() {
        return description;
    }

}
