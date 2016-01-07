package com.victor.midas.train.score;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.score.StockScoreRank;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;
import com.victor.utilities.algorithm.search.TopKElements;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * used for calculate score, and record score
 */
public class GeneralScoreManager implements ScoreManager {

    private static final Logger logger = Logger.getLogger(GeneralScoreManager.class);

    private String indexName;
    private List<StockVo> stocks;
    private Map<String, StockVo> name2stock;    // stock name map to date index
    private StockVo indexSH;
    private List<StockVo> tradableStocks;
    private int tradableCnt;
    private int[] dates;                        // benchmark time line
    private int index;                          // benchmark stock's date index
    private int len;                            // benchmark stock's date len

    private List<StockScoreRecord> scoreRecords;

    private boolean isBigDataSet;

    public GeneralScoreManager(List<StockVo> stocks, String indexName) throws Exception {
        this.stocks = stocks;
        this.indexName = indexName;
        initStocks();
    }

    public void process() throws Exception {
        logger.info("start score simulation ...");
        scoreRecords = new ArrayList<>();
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
                    scores = (double[])stock.queryCmpIndex(indexName);
                    stockScores.add(new StockScore(stock.getStockName(), scores[index], cob));
                }
            }
            /*** move iterator forward */
            for (StockVo stock : tradableStocks) {
                stock.advanceIndex(cob);
            }
            /*** find best stock with top score */
            stockScores = ArrayHelper.array2list(TopKElements.getFirstK(stockScores, 5));
            ScoreHelper.perfCollect(stockScores, cob, perfCollector, scoreRecords);
        }

        logger.info("result : " + perfCollector.toString());
        FileUtils.write(new File("D:\\stock_performance.txt"), perfCollector.toPerfString());
    }

    /**
     * in strategy, it use related calculator only, so it is needed to use all calculators to init
     */
    private void initStocks() throws MidasException, IOException {
        IndexCalculator calculator = new IndexCalculator(stocks, indexName);
        calculator.calculate();
        isBigDataSet = calculator.isBigDataSet();

        StockFilterUtil filterUtil = calculator.getFilterUtil();
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

    public List<StockScoreRecord> getScoreRecords() {
        return scoreRecords;
    }

    public List<StockVo> getStocks() {
        return stocks;
    }
}
