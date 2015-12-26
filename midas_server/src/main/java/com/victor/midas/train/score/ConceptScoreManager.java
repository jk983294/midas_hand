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
public class ConceptScoreManager implements ScoreManager {

    private static final Logger logger = Logger.getLogger(ConceptScoreManager.class);

    private List<StockVo> stocks;
    private List<StockCrawlData> crawlDatas;
    private List<StockConceptStats> stockConceptStatses;
    private Map<StockConcept, StockConceptStats> concept2stats;
    private Map<String, StockVo> name2stock;    // stock name map to date index
    private StockVo indexSH;
    private List<StockVo> tradableStocks;
    private int tradableCnt;
    private int[] dates;                        // benchmark time line
    private int index;                          // benchmark stock's date index
    private int len;                            // benchmark stock's date len

    private List<StockScoreRecord> scoreRecords;

    private boolean isBigDataSet;

    public ConceptScoreManager(List<StockVo> stocks, List<StockCrawlData> crawlData) throws Exception {
        this.stocks = stocks;
        this.crawlDatas = crawlData;
        initStocks();
    }

    public void process() throws Exception {
        logger.info("start score simulation ...");
        scoreRecords = new ArrayList<>();
        double[] scores, avgAmplitude;
        PerfCollector conceptPerfCollector = new PerfCollector(name2stock);

        int cob;
        for (int i = 0; i < len; i++) {
            cob = dates[i];
            /*** aggregate score for that concept to get mean */
            for (StockConceptStats conceptStats : stockConceptStatses) {
                conceptStats.getScoreStatistics().clear();
                for(StockVo stock : conceptStats.getStocks()){
                    if(stock == null){
                        continue;
                    }
                    index = stock.getCobIndex();
                    if(stock.isSameDayWithIndex(cob)){
                        scores = (double[])stock.queryCmpIndex("ssr");
                        conceptStats.getScoreStatistics().addValue(scores[index]);
                    }
                }
                conceptStats.setScore(conceptStats.getScoreStatistics().getMean());
            }
            /*** find best stock with top score */
            List<StockConceptStats> topConcepts = ArrayHelper.array2list(TopKElements.getFirstK(stockConceptStatses, 5));
            List<StockScore> conceptScores = new ArrayList<>();
            for (StockConceptStats conceptStats : topConcepts) {
                StockVo bestStock = null;
                double bestScore = -100;
                for(StockVo stock : conceptStats.getStocks()){
                    if(stock == null){
                        continue;
                    }
                    index = stock.getCobIndex();
                    if(stock.isSameDayWithIndex(cob)){
                        scores = (double[])stock.queryCmpIndex("ssr");
                        avgAmplitude = (double[])stock.queryCmpIndex("avgAmplitude");
                        if(scores[index] * avgAmplitude[index] > bestScore){
                            bestStock = stock;
                            bestScore = scores[index] * avgAmplitude[index] * 10;
                        }
                    }
                }
                if(bestStock != null){
                    conceptScores.add(new StockScore(bestStock.getStockName(), conceptStats.getConcept().getName(), bestScore));
                }
            }
            /*** move iterator forward */
            for (StockVo stock : tradableStocks) {
                stock.advanceIndex(cob);
            }
            /*** find best stock with top score */
            ScoreHelper.perfCollect(conceptScores, cob, conceptPerfCollector, scoreRecords);
        }
        logger.info("concept result : " + conceptPerfCollector.toString());
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

        initConcept();

        logger.info("init stock finished...");
    }

    /**
     * add all stock object ref into conceptStats,
     * count how many stocks in each concept,
     * remove those conceptStats which have too many or little stocks,
     * aggregate all stocks which doesn't have concept into this special conceptStats
     */
    private void initConcept() throws IOException {
        Set<StockConcept> concepts = new HashSet<>();
        concept2stats = new HashMap<>();
        for(StockCrawlData crawlData : crawlDatas){
            concepts.addAll(crawlData.getConcepts());
        }
        for(StockConcept concept : concepts){
            StockConceptStats conceptStats = new StockConceptStats(concept);
            concept2stats.put(concept, conceptStats);
        }
        /*** add all stock object ref into conceptStats */
        for(StockCrawlData crawlData : crawlDatas){
            StockVo stock = name2stock.get(crawlData.getStockCode());
            for (StockConcept concept : crawlData.getConcepts()){
                concept2stats.get(concept).addStock(stock);
            }
        }
        /*** count how many stocks in each concept */
        Map<Integer, Integer> stock2cnts = new TreeMap<>();
        for (StockConcept concept : concept2stats.keySet()){
            int stockCnt = concept2stats.get(concept).getStocks().size();
            if(stock2cnts.containsKey(stockCnt)){
                stock2cnts.put(stockCnt, stock2cnts.get(stockCnt) + 1);
            } else {
                stock2cnts.put(stockCnt, 1);
            }
//            if(stockCnt > 100){
//                logger.info(concept2stats.get(concept).getConcept().getName() + " : " + stockCnt);
//            }
        }
        /*** remove those conceptStats which have too many or little stocks */
        Set<StockConcept> toRemoveConcepts = new HashSet<StockConcept>();
        for (StockConcept concept : concept2stats.keySet()){
            int stockCnt = concept2stats.get(concept).getStocks().size();
            if(stockCnt > 150 || stockCnt < 8){
                toRemoveConcepts.add(concept);
            }
        }
        for (StockConcept concept : toRemoveConcepts){
            concept2stats.remove(concept);
        }
        /*** aggregate all stocks which doesn't have concept into this special conceptStats */
        StockConcept noConcepts = new StockConcept("noConcepts", "noConcepts");
        StockConceptStats noConceptStats = new StockConceptStats(noConcepts);
        for(StockCrawlData crawlData : crawlDatas){
            if(!ArrayHelper.containAny(concept2stats.keySet(), crawlData.getConcepts())){
                noConceptStats.addStock(name2stock.get(crawlData.getStockCode()));
                //logger.info("stock no concept found : " + crawlData.getStockCode());
            }
        }
        if(noConceptStats.getStocks().size() > 0){
            concept2stats.put(noConcepts, noConceptStats);
        }
        stockConceptStatses = ArrayHelper.array2list(concept2stats.values());
    }


    public boolean isBigDataSet() {
        return isBigDataSet;
    }

    public List<StockScoreRecord> getScoreRecords() {
        return scoreRecords;
    }
}
