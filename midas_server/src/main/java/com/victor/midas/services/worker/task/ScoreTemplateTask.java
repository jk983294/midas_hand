package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.score.StockRevertScoreRank;
import com.victor.midas.dao.ConceptScoreDao;
import com.victor.midas.dao.ScoreDao;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.train.ScoreTemplateManager;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.PerformanceUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class ScoreTemplateTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(ScoreTemplateTask.class);
	private static final String description = "Score Template Task";

	private StocksService stocksService;

    private ScoreDao scoreDao;
    private ConceptScoreDao conceptScoreDao;
    /** default come from DB*/
    private boolean isFromFileSystem = false;

    public ScoreTemplateTask(TaskDao taskdao, StocksService stocksService, boolean isFromFileSystem, List<String> params) {
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
        CalcParameter parameter = new CalcParameter();
        IndexCalcBase indexCalcBase = null;
        if(params == null || params.size() == 0){
            throw new MidasException("no calculator is specified.");
        } else if(params.get(0).equalsIgnoreCase("StockRevertScoreRank")){
            indexCalcBase = new StockRevertScoreRank(parameter);
        }
        List<StockVo> stocks = getAllStock();

        ScoreTemplateManager manager = new ScoreTemplateManager(parameter, indexCalcBase, stocks);

        logger.info( "start score template ...");
        manager.process();

        logger.info( "start save results ...");
        if(isFromFileSystem || !manager.isBigDataSet()){
            stocksService.saveStocks(stocks);               // maybe train strategy has generate new data
        }
        scoreDao.save(manager.getStockScoreRecords());

        PerformanceUtil.manuallyGC(stocks);

		logger.info( description + " complete...");
	}

}
