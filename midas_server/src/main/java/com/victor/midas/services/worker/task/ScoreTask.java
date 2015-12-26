package com.victor.midas.services.worker.task;

import com.victor.midas.dao.ConceptScoreDao;
import com.victor.midas.dao.ScoreDao;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.train.ScoreManager;
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
        CmdParameter cmdParameter = CmdParameter.ma_score;
        if(params.size() > 0){
            cmdParameter = CmdParameter.valueOf(params.get(0));
        }

        List<StockCrawlData> crawlData = stocksService.queryAllStockCrawlData();
        List<StockVo> stocks = getAllStock();

        ScoreManager manager = new ScoreManager(stocks, crawlData);

        logger.info( "start score ...");
        manager.process();

        scoreDao.save(manager.getStockScoreRecords());
        conceptScoreDao.save(manager.getStockConceptScoreRecords());
        if(isFromFileSystem || !manager.isBigDataSet()){
            logger.info("start save stocks ...");
            stocksService.saveStocks(stocks);               // maybe train strategy has generate new data
        }

        PerformanceUtil.manuallyGC(stocks);

		logger.info( description + " complete...");
	}

}
