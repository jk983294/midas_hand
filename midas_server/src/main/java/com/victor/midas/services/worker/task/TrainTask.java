package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.macd.IndexMacdAdvancedSignal;
import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.train.SingleParameterTrainer;
import com.victor.midas.train.common.Trainee;
import com.victor.midas.train.score.GeneralScoreManager;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.JsonHelper;
import com.victor.utilities.utils.OsHelper;
import com.victor.utilities.utils.PerformanceUtil;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Scope("prototype")
public class TrainTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(TrainTask.class);
	private static final String description = "Train Task";

	@Override
	public void doTask() throws Exception {
        switch (cmdType){
            case trainSingle: trainSingle(); break;
            default: throw new MidasException("this cmd type is not support in train task.");
        }
        logger.info( description + " complete...");
	}

    private void trainSingle() throws Exception {
        if(params != null && params.size() == 4){
            SingleParameterTrainer trainer = null;
            String targetIndexName = IndexMacdAdvancedSignal.INDEX_NAME;
            if(params != null && params.size() > 0){
                targetIndexName = params.get(0);
            }

            if(RegExpHelper.isInts(params.subList(1, 4))){
                trainer = new SingleParameterTrainer(Integer.valueOf(params.get(1)), Integer.valueOf(params.get(2)), Integer.valueOf(params.get(3)));
            } else if(RegExpHelper.isDoubles(params.subList(1, 4))){
                trainer = new SingleParameterTrainer(Double.valueOf(params.get(1)), Double.valueOf(params.get(2)), Double.valueOf(params.get(3)));
            }
            List<StockVo> stocks = stocksService.queryAllStock();
            if(trainer != null){
                Trainee trainee = new GeneralScoreManager(stocks, targetIndexName);
                trainee.setIsInTrain(true);
                trainer.setTrainee(trainee);
                trainer.process();
//                FileUtils.write(new File(OsHelper.getPathByOs("stock_train_result.txt")), new JsonHelper().toJson(trainer.getResults()));
                stocksService.saveSingleParameterTrainResults(trainer.getResults());
            }
            PerformanceUtil.manuallyGC(stocks);
        } else {
            throw new MidasException("training parameter not correct");
        }

    }

    @Override
    public String getDescription() {
        return description;
    }

}
