package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.chan.ChanMorphologyExtend;
import com.victor.midas.calculator.chan.model.ChanResults;
import com.victor.midas.calculator.perf.PerfCalc;
import com.victor.midas.dao.ConceptScoreDao;
import com.victor.midas.dao.ScoreDao;
import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.IoHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class PerfTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(PerfTask.class);
	private static final String description = "Perf Task";

	private StocksService stocksService;

	public PerfTask(TaskDao taskdao, StocksService stocksService, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;

	}

	@Override
	public void doTask() throws Exception {
        PerfCalc perfCalc = new PerfCalc(stocksService.getStockBasicInfo());
        perfCalc.calculate();
        IoHelper.toJsonFileWithIndent(perfCalc.getResult(), "D:\\MktData\\RawData\\dan stock performance.json");
		logger.info( description + " complete...");
	}

}
