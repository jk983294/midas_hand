package com.victor.midas.train.score;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.model.vo.score.StockSeverity;
import com.victor.midas.train.common.MidasTrainHelper;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.train.common.TrainOptionApply;
import com.victor.midas.train.common.Trainee;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import com.victor.utilities.algorithm.search.TopKElements;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.JsonHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * used for calculate score, and record score
 */
public class GeneralScoreManager implements ScoreManager, Trainee, TrainOptionApply {

    private static final Logger logger = Logger.getLogger(GeneralScoreManager.class);

    private String indexName;
    private IndexCalculator calculator;
    private List<StockVo> stocks;
    private List<StockVo> tradableStocks;
    private int tradableCnt;
    private int[] dates;                        // benchmark time line
    private int index;                          // benchmark stock's date index
    private int len;                            // benchmark stock's date len

    private List<StockScoreRecord> scoreRecords;
    private PerfCollector perfCollector;
    public boolean isBigDataSet, isInTrain = false, useQuitSignal = false;
    public MidasTrainOptions options;

    public GeneralScoreManager(List<StockVo> stocks, String indexName) throws Exception {
        this.stocks = stocks;
        this.indexName = indexName;
        initStocks();
    }

    public void process() throws Exception {
        logger.info("start score simulation ...");
        double[] scores, shBadDepth;
        StockSeverity severity;
        shBadDepth = (double[])calculator.getFilterUtil().getMarketIndex().queryCmpIndex("badDepth");
        int cob;
        for (int i = 0; i < len; i++) {
            cob = dates[i];
            /*** iterator through all stocks to get scores */
            List<StockScore> stockScores = new ArrayList<>();
            for (int j = 0; j < tradableCnt; j++) {
                StockVo stock = tradableStocks.get(j);
                index = stock.getCobIndex();
                if(stock.isSameDayWithIndex(cob)){
                    scores = (double[])stock.queryCmpIndex(indexName);
                    if(!Double.isNaN(scores[index])){
                        if(useQuitSignal){
                            if(scores[index] > 1d){ // buy signal
                                StockScore score = new StockScore(stock.getStockName(), scores[index], cob);
                                score.applyOptions(options);
                                MidasTrainHelper.getHoldingTime(scores, score, index, stock);
                                if(score.sellIndex > 0){
                                    stockScores.add(score);
                                }
                            }
                        } else {    // no signal, every stock will take account
                            stockScores.add(new StockScore(stock.getStockName(), scores[index], cob));
                        }
                    } else {
                        throw new MidasException("NaN score found for "+ stock.getStockName() + " cob " + stock.getDatesInt()[index]);
                    }
                }
            }
            /*** move iterator forward */
            for (StockVo stock : tradableStocks) {
                stock.advanceIndex(cob);
            }
            /*** find best stock with top score */
            if(options.selectTops){
                stockScores = ArrayHelper.array2list(TopKElements.getFirstK(stockScores, 5));
            }
            if(shBadDepth[i] > -1d){
                severity = StockSeverity.Normal;
            } else {
                severity = StockSeverity.Warning;
            }
            ScoreHelper.perfCollect(stockScores, cob, perfCollector, scoreRecords, severity);
        }

        if(!isInTrain){
            logger.info("result : " + perfCollector.toString());
            FileUtils.write(new File("E:\\stock_performance.txt"), perfCollector.toPerfString());
            FileUtils.write(new File("E:\\stock_performance_by_name.txt"), new JsonHelper().toJson(perfCollector.getName2scores()));
        }
    }

    /**
     * this is differ from initForTrain, everything in initStocks will be initialized once
     */
    private void initStocks() throws MidasException, IOException {
        calculator = new IndexCalculator(stocks, indexName);
        applyOptions(calculator.options);
        options = calculator.options;
        calculator.calculate();
        isBigDataSet = calculator.isBigDataSet();
        StockFilterUtil filterUtil = calculator.getFilterUtil();
        tradableStocks = filterUtil.getTradableStocks();
        dates = filterUtil.getMarketIndex().getDatesInt();
        len = dates.length;
        tradableCnt = tradableStocks.size();
        perfCollector = new PerfCollector(filterUtil.getName2stock());

        initForTrain();
        logger.info("init stock finished...");
    }

    /**
     * init all cob indexes, init a new PerfCollector for collection
     */
    private void initForTrain(){
        perfCollector.clear();
        scoreRecords = new ArrayList<>();
        for(StockVo stock : tradableStocks){
            stock.setCobIndex(0);
        }
        index = 0;
    }

    public boolean isBigDataSet() {
        return isBigDataSet;
    }

    public List<StockScoreRecord> getScoreRecords() {
        return scoreRecords;
    }

    public List<StockVo> getStocks() {
        return stocks;
    }

    @Override
    public SingleParameterTrainResult getPerformance() {
        return perfCollector.getResult();
    }

    @Override
    public void apply(CalcParameter parameter) throws Exception {
        calculator.apply(parameter);
        initForTrain();
        process();
    }

    @Override
    public void setIsInTrain(boolean isInTrain) {
        this.isInTrain = isInTrain;
        perfCollector.setIsInTrain(isInTrain);
    }

    @Override
    public void applyOptions(MidasTrainOptions options) {
        if(options != null){
            this.useQuitSignal = options.useSignal;
        }
    }
}
