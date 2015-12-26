package com.victor.midas.train.score;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.calculator.score.StockScoreRank;
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
public class MaScoreManager {

    private static final Logger logger = Logger.getLogger(MaScoreManager.class);

    private List<StockVo> stocks;
    private Map<String, StockVo> name2stock;    // stock name map to date index
    private StockVo indexSH;
    private List<StockVo> tradableStocks;
    private int tradableCnt;
    private int[] dates;                        // benchmark time line
    private int index;                          // benchmark stock's date index
    private int len;                            // benchmark stock's date len

    private List<StockScoreRecord> stockScoreRecords;

    private boolean isBigDataSet;

    public MaScoreManager(List<StockVo> stocks) throws Exception {
        this.stocks = stocks;
        initStocks();
    }

    public void process() throws Exception {
        logger.info("start score simulation ...");
        stockScoreRecords = new ArrayList<>();
        double[] scores, avgAmplitude;
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
            ScoreHelper.perfCollect(stockScores, cob, perfCollector, stockScoreRecords);
        }

        logger.info("result : " + perfCollector.toString());
    }

    /**
     * in strategy, it use related calculator only, so it is needed to use all calculators to init
     */
    private void initStocks() throws MidasException, IOException {
        IndexCalculator calculator = new IndexCalculator(stocks, StockScoreRank.INDEX_NAME);
        calculator.setBigDataSet(false);
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

    public List<StockScoreRecord> getStockScoreRecords() {
        return stockScoreRecords;
    }
}
