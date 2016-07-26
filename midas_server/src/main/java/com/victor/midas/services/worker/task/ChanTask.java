package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.chan.ChanMorphologyExtend;
import com.victor.midas.calculator.chan.model.ChanResults;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.services.worker.loader.StockDataLoader;
import com.victor.utilities.utils.IoHelper;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class ChanTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(ChanTask.class);
	private static final String description = "Chan Task";

    /** default come from DB*/
    private boolean isFromFileSystem = false;
    private Map<String,String> filepath2prefix;

    public void init(Map<String, String> filepath2prefix) {
        this.filepath2prefix = filepath2prefix;
        isFromFileSystem = true;
    }

    private List<StockVo> getAllStock() throws Exception {
        if(isFromFileSystem){
            return (List<StockVo>)(new StockDataLoader().load("F:\\Data\\MktData\\ALL"));
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

    @Override
    public String getDescription() {
        return description;
    }

}
