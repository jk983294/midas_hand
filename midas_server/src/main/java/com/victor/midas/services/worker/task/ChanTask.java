package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.chan.ChanMorphologyExtend;
import com.victor.midas.calculator.chan.model.ChanResults;
import com.victor.midas.dao.ConceptScoreDao;
import com.victor.midas.dao.ScoreDao;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.utilities.utils.IoHelper;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ChanTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(ChanTask.class);
	private static final String description = "Chan Task";

	private StocksService stocksService;

    private ScoreDao scoreDao;
    private ConceptScoreDao conceptScoreDao;
    /** default come from DB*/
    private boolean isFromFileSystem = false;
    private Map<String,String> filepath2prefix;

	public ChanTask(TaskDao taskdao, StocksService stocksService, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
        this.scoreDao = stocksService.getScoreDao();
        this.conceptScoreDao = stocksService.getConceptScoreDao();
	}

    public ChanTask(TaskDao taskdao, StocksService stocksService, Map<String, String> filepath2prefix, List<String> params) {
        super( description, taskdao, params);
        this.stocksService = stocksService;
        this.scoreDao = stocksService.getScoreDao();
        this.conceptScoreDao = stocksService.getConceptScoreDao();
        this.filepath2prefix = filepath2prefix;
        isFromFileSystem = true;
    }

    private List<StockVo> getAllStock() throws Exception {
        if(isFromFileSystem){
            return new MktDataTask().getStockFromFileSystem("F:\\Data\\MktData\\ALL");
        } else {
            return stocksService.queryAllStock();
        }
    }

	@Override
	public void doTask() throws Exception {
        String code = "SH603118"; //SZ002673
        if(params.size() > 0){
            code = params.get(0);
        }
//        List<StockVo> stocks = getAllStock();
//
//        ChanManager manager = new ChanManager(new CalcParameter(), stocks);
//
//        logger.info( "start score ...");
//        manager.process();
//
//        logger.info( "start save results ...");
//        if(isFromFileSystem || !manager.isBigDataSet()){
//            stocksService.saveStocks(stocks);               // maybe train strategy has generate new data
//        }
//        scoreDao.save(manager.getStockScoreRecords());

        StockVo stock = stocksService.getStockDao().queryStock(code); //SZ002673
        ChanMorphologyExtend indexCalcbase = new ChanMorphologyExtend(new CalcParameter());
        indexCalcbase.calculate(stock);
        ChanResults results = new ChanResults(indexCalcbase.getMergedKLines(), indexCalcbase.getFractalKeyPoints(), indexCalcbase.getStrokes());
        IoHelper.toJsonFileWithIndent(results, "D:\\chan.json");

//        PerformanceUtil.manuallyGC(stocks);

		logger.info( description + " complete...");
	}

}
