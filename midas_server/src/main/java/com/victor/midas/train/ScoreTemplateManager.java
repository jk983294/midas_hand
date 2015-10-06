package com.victor.midas.train;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.concept.StockConcept;
import com.victor.midas.model.vo.concept.StockConceptStats;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import com.victor.utilities.algorithm.search.TopKElements;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * used for calculate score, and record score
 */
public class ScoreTemplateManager {

    private static final Logger logger = Logger.getLogger(ScoreTemplateManager.class);

    private List<StockVo> stocks;
    private Map<String, StockVo> name2stock;    // stock name map to date index
    private StockVo indexSH;
    private List<StockVo> tradableStocks;
    private int tradableCnt;
    private int[] dates;                        // benchmark time line
    private int index;                          // benchmark stock's date index
    private int len;                            // benchmark stock's date len

    private IndexCalcbase indexCalcbase;

    private List<StockScoreRecord> stockScoreRecords, stockConceptScoreRecords;

    private boolean isBigDataSet;

    public ScoreTemplateManager(CalcParameter parameter, IndexCalcbase indexCalcbase, List<StockVo> stocks) throws Exception {
        this.stocks = stocks;
        this.indexCalcbase = indexCalcbase;
        initStocks(parameter);
    }

    public void process() throws Exception {
        logger.info("start score simulation ...");
        stockScoreRecords = new ArrayList<>();
        stockConceptScoreRecords = new ArrayList<>();
        double[] scores;
        PerfCollector perfCollector = new PerfCollector(name2stock);

        int cob;
        for (int i = 0; i < len; i++) {
            cob = dates[i];
            /*** iterator through all stocks to get scores */
            List<StockScore> stockScores = new ArrayList<>();
            for (int j = 0; j < tradableCnt; j++) {
                StockVo stock = tradableStocks.get(j);
                index = stock.getCobIndex();
                if(stock.isSameDayWithIndex(cob)){
                    scores = (double[])stock.queryCmpIndex("ssr");
                    stockScores.add(new StockScore(stock.getStockName(), scores[index]));
                }
            }
            /*** move iterator forward */
            for (StockVo stock : tradableStocks) {
                stock.advanceIndex(cob);
            }
            /*** find best stock with top score */
            stockScores = ArrayHelper.array2list(TopKElements.getFirstK(stockScores, 5));
            perfCollect(stockScores, cob, perfCollector, stockScoreRecords);
        }

        logger.info("result : " + perfCollector.toString());
    }

    private void perfCollect(List<StockScore> stockScores, int cob, PerfCollector perfCollector, List<StockScoreRecord> scoreRecords) throws MidasException {
        if(stockScores.size() > 0){
            StockScoreRecord stockScoreRecord = new StockScoreRecord(cob, stockScores);
            scoreRecords.add(stockScoreRecord);
            perfCollector.addRecord(stockScoreRecord);
        }
    }

    /**
     * in strategy, it use related calculator only, so it is needed to use all calculators to init
     */
    private void initStocks(CalcParameter parameter) throws MidasException, IOException {
        IndexCalculator calculator = new IndexCalculator(stocks, parameter);
        calculator.calculate();
        isBigDataSet = calculator.isBigDataSet();

        for(StockVo stock : stocks){
            try {
                indexCalcbase.calculate(stock);
            } catch (Exception e){
                logger.error(e);
                throw new MidasException("problem meet when calculate index for " + stock, e);
            }
        }

        StockFilterUtil filterUtil = new StockFilterUtil(stocks);
        filterUtil.filter();
        indexSH = filterUtil.getIndexSH();
        tradableStocks = filterUtil.getTradableStocks();

        dates = indexSH.getDatesInt();
        for(StockVo stock : tradableStocks){
            stock.setCobIndex(0);
        }
        len = dates.length;
        tradableCnt = tradableStocks.size();
        index = 0;
        name2stock = filterUtil.getName2stock();
        logger.info("init stock finished...");
    }


    public boolean isBigDataSet() {
        return isBigDataSet;
    }

    public List<StockScoreRecord> getStockScoreRecords() {
        return stockScoreRecords;
    }

    public List<StockScoreRecord> getStockConceptScoreRecords() {
        return stockConceptScoreRecords;
    }
}
