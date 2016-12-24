package com.victor.midas.train.score;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.model.vo.score.StockSeverity;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.train.common.TrainOptionApply;
import com.victor.midas.train.common.Trainee;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
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

    private String targetIndexName;
    private IndexCalculator calculator;
    private List<StockVo> stocks;
    private int[] dates;                        // benchmark time line
    private int len;                            // benchmark stock's date len

    private List<StockScoreRecord> scoreRecords;
    public PerfCollector perfCollector;
    public boolean isBigDataSet, isInTrain = false, useSignal = false;
    public MidasTrainOptions options;

    public GeneralScoreManager(List<StockVo> stocks, String targetIndexName) throws Exception {
        this.stocks = stocks;
        this.targetIndexName = targetIndexName;
        initStocks();
    }

    public void process(CalcParameter parameter) throws Exception {
        initForTrain();

        calculator.apply(parameter);
        List<List<StockScore>> list = perfCollector.getAllScoreListSortByCob();

        logger.info("start score simulation ...");
        double[] shBadDepth;
        StockSeverity severity;
        shBadDepth = (double[])calculator.getFilterUtil().getMarketIndex().queryCmpIndex("badDepth");
        int cob;

        for (int i = 0; i < len; i++) {
            cob = dates[i];
            List<StockScore> stockScores = list.get(i);

            if(shBadDepth[i] > -1d){
                severity = StockSeverity.Normal;
            } else {
                severity = StockSeverity.Warning;
                if(stockScores.size() == 0){                    // keep all warning days
                    stockScores.add(new StockScore("fake", -1d, cob));
                }
            }
            if(i == len - 1 && stockScores.size() == 0){        // always keep last day
                stockScores.add(new StockScore("fake", -1d, cob));
            }

            /**
             * no matter what severity is that cob, StockScoreRecord will always be recorded,
             * but only those normal cob's data will be performance collected
             */
            StockScoreRecord stockScoreRecord = new StockScoreRecord(cob, stockScores);
            scoreRecords.add(stockScoreRecord);
            stockScoreRecord.setSeverity(severity);
            if(severity.ordinal() <= StockSeverity.Normal.ordinal()){
                perfCollector.addRecord(stockScoreRecord);
            }
        }

        if(!isInTrain){
            if(scoreRecords.size() > 0){
                logger.info("today score result : " + scoreRecords.get(scoreRecords.size() - 1));
            }
            logger.info("result : " + perfCollector.toString());
//            FileUtils.write(new File("E:\\stock_performance.txt"), perfCollector.toPerfString());
//            FileUtils.write(new File("E:\\stock_performance_by_name.txt"), new JsonHelper().toJson(perfCollector.getName2scores()));
        }
    }

    /**
     * this is differ from initForTrain, everything in initStocks will be initialized once
     */
    private void initStocks() throws MidasException, IOException {
        calculator = new IndexCalculator(stocks, targetIndexName);
        applyOptions(calculator.options);
        options = calculator.options;
        //calculator.calculate();
        isBigDataSet = calculator.isBigDataSet();
        StockFilterUtil filterUtil = calculator.getFilterUtil();
        dates = filterUtil.getMarketIndex().getDatesInt();
        len = dates.length;
        perfCollector = calculator.targetCalculator.getPerfCollector();

        logger.info("init stock finished...");
    }

    /**
     * init all cob indexes, init a new PerfCollector for collection
     */
    private void initForTrain(){
        scoreRecords = new ArrayList<>();
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
        process(parameter);
    }

    @Override
    public void setIsInTrain(boolean isInTrain) {
        this.isInTrain = isInTrain;
        perfCollector.setIsInTrain(isInTrain);
    }

    @Override
    public void applyOptions(MidasTrainOptions options) {
        if(options != null){
            this.useSignal = options.useSignal;
        }
    }
}
