package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.score.StockRevertScoreRank;
import com.victor.midas.calculator.score.StockScoreRank;
import com.victor.midas.dao.ConceptScoreDao;
import com.victor.midas.dao.ScoreDao;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.train.score.ConceptScoreManager;
import com.victor.midas.train.score.GeneralScoreManager;
import com.victor.midas.train.score.ScoreManager;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.PerformanceUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class ScoreTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(ScoreTask.class);
	private static final String description = "Score Task";

	private StocksService stocksService;

    private ScoreDao scoreDao;
    private ConceptScoreDao conceptScoreDao;
    /** default come from DB*/
    private boolean isFromFileSystem = false;

    public ScoreTask(TaskDao taskdao, StocksService stocksService, boolean isFromFileSystem, List<String> params) {
        super(description, taskdao, params);
        this.stocksService = stocksService;
        this.scoreDao = stocksService.getScoreDao();
        this.conceptScoreDao = stocksService.getConceptScoreDao();
        this.isFromFileSystem = isFromFileSystem;
    }

    private List<StockVo> getAllStock() throws Exception {
        if(isFromFileSystem){
            return MktDataTask.getStockFromFileSystem("D:\\MktData\\RawData\\ALL");
        } else {
            return stocksService.queryAllStock();
        }
    }

	@Override
	public void doTask() throws Exception {
        CmdParameter cmdParameter = CmdParameter.getParameter(CmdParameter.score_ma, params, 0);

        ScoreManager manager = getScoreManager(cmdParameter);

        logger.info( "start score ...");
        manager.process();
        saveResults(manager, cmdParameter);

        PerformanceUtil.manuallyGC(manager.getStocks());

		logger.info( description + " complete...");
	}

    private void saveResults(ScoreManager manager, CmdParameter cmdParameter) throws MidasException {
        scoreDao.save(manager.getScoreRecords());
        switch(cmdParameter){
            case score_ma:  scoreDao.save(manager.getScoreRecords()); break;
            case score_concept: conceptScoreDao.save(manager.getScoreRecords()); break;
            default : logger.error("no such parameter in score task.");
        }
        if(isFromFileSystem || !manager.isBigDataSet()){
            logger.info("start save stocks ...");
            stocksService.saveStocks(manager.getStocks());               // maybe train strategy has generate new data
        }
    }

    private ScoreManager getScoreManager(CmdParameter cmdParameter) throws Exception {
        List<StockVo> stocks = getAllStock();
        String indexName = getIndexName(cmdParameter);
        switch(cmdParameter){
            case score_ma:
            case score_revert: return new GeneralScoreManager(stocks, indexName);
            case score_concept: {
                List<StockCrawlData> crawlData = stocksService.queryAllStockCrawlData();
                return new ConceptScoreManager(stocks, indexName, crawlData);
            }
            default : logger.error("no such ScoreManager in score task.");
        }
        return new GeneralScoreManager(stocks, indexName);
    }

    private String getIndexName(CmdParameter cmdParameter) throws Exception {
        switch(cmdParameter){
            case score_ma:  return StockScoreRank.INDEX_NAME;
            case score_revert: return StockRevertScoreRank.INDEX_NAME;
            case score_concept: return StockScoreRank.INDEX_NAME;
            default : logger.error("no such IndexName in score task.");
        }
        return StockScoreRank.INDEX_NAME;
    }

}
