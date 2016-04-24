package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.macd.IndexMacdAdvancedSignal;
import com.victor.midas.calculator.revert.PriceCrashRevertSignal;
import com.victor.midas.calculator.score.StockRevertScoreRank;
import com.victor.midas.calculator.score.StockScoreRank;
import com.victor.midas.calculator.score.StockSupportScoreRank;
import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.model.common.CmdType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.dao.TrainDao;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.train.SingleParameterTrainer;
import com.victor.midas.train.TrainManager;
import com.victor.midas.train.common.Trainee;
import com.victor.midas.train.score.GeneralScoreManager;
import com.victor.midas.train.strategy.single.SrStrategyS;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.JsonHelper;
import com.victor.utilities.utils.PerformanceUtil;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

public class TrainTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(TrainTask.class);
	private static final String description = "Train Task";

	private StocksService stocksService;

    private TrainDao trainDao;
    private CmdType cmdType;

	public TrainTask(TaskDao taskdao, StocksService stocksService, List<String> params, CmdType cmdType) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
        this.trainDao = stocksService.getTrainDao();
        this.cmdType = cmdType;
	}

	@Override
	public void doTask() throws Exception {
        switch (cmdType){
            case trainStrategy: trainStrategy(); break;
            case trainSingle: trainSingle(); break;
            default: throw new MidasException("this cmd type is not support in train task.");
        }
        logger.info( description + " complete...");
	}

    private void trainSingle() throws Exception {
        if(params != null && params.size() == 4){
            SingleParameterTrainer trainer = null;
            CmdParameter cmdParameter = CmdParameter.getParameter(CmdParameter.score_ma, params, 0);
            String indexName = CmdParameter.getIndexName(cmdParameter);
            if(RegExpHelper.isInts(params.subList(1, 4))){
                trainer = new SingleParameterTrainer(Integer.valueOf(params.get(1)), Integer.valueOf(params.get(2)), Integer.valueOf(params.get(3)));
            } else if(RegExpHelper.isDoubles(params.subList(1, 4))){
                trainer = new SingleParameterTrainer(Double.valueOf(params.get(1)), Double.valueOf(params.get(2)), Double.valueOf(params.get(3)));
            }
            List<StockVo> stocks = stocksService.queryAllStock();
            if(trainer != null){
                Trainee trainee = new GeneralScoreManager(stocks, indexName);
                trainee.setIsInTrain(true);
                trainer.setTrainee(trainee);
                trainer.process();
                FileUtils.write(new File("E:\\stock_train_result.txt"), new JsonHelper().toJson(trainer.getResults()));
                stocksService.saveSingleParameterTrainResults(trainer.getResults());
                //trainDao.saveTrainResult(manager.getTrainResult());
            }
            PerformanceUtil.manuallyGC(stocks);
        } else {
            throw new MidasException("training parameter not correct");
        }

    }

    private void trainStrategy() throws Exception {
        List<StockVo> stocks = stocksService.queryAllStock();
        CalcParameter parameter = new CalcParameter();
        // KLineStrategyS LgtStrategyS GpStrategyS
        String strategyName = SrStrategyS.STRATEGY_NAME;

        TrainManager manager = new TrainManager(parameter, stocks, strategyName);

        logger.info( "start train ...");
        manager.process();

        logger.info( "start save results ...");
        if(!manager.isBigDataSet()){
            stocksService.saveStocks(stocks);               // maybe train strategy has generate new data
        }
        trainDao.saveTrainResult(manager.getTrainResult());

        PerformanceUtil.manuallyGC(stocks);
    }

}
