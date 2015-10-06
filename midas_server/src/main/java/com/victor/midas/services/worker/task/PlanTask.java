package com.victor.midas.services.worker.task;

import com.victor.midas.dao.MiscDao;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.dao.TrainDao;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.train.TradePlanManager;
import com.victor.utilities.utils.PerformanceUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class PlanTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(PlanTask.class);
	private static final String description = "Plan Task";

	private StocksService stocksService;

    private TrainDao trainDao;

    private MiscDao miscDao;

	public PlanTask(TaskDao taskdao, StocksService stocksService, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
        this.trainDao = stocksService.getTrainDao();
        this.miscDao = stocksService.getMiscDao();
	}

	@Override
	public void doTask() throws Exception {
        List<StockVo> stocks = stocksService.queryAllStock();
        // TODO get parameter from service
        CalcParameter parameter = new CalcParameter();
        // KLineStrategyS LgtStrategyS GpStrategyS
        String strategyName = "GpStrategyS";

        TradePlanManager manager = new TradePlanManager(parameter, stocks, strategyName);

        logger.info( "start plan ...");
        manager.process();

        logger.info( "start save results ...");
        if(!manager.isBigDataSet()){
            stocksService.saveStocks(stocks);               // maybe train strategy has generate new data
        }
        trainDao.saveTrainResult(manager.getTrainResult());
        stocksService.getDayFocusDao().saveDayFocus(manager.getFocus());
        //miscDao.saveMisc(manager.getFocus());

        PerformanceUtil.manuallyGC(stocks);

		logger.info( description + " complete...");
	}

}
