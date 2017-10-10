package com.victor.midas.train.perf;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.model.vo.score.StockScoreState;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.TopKHeap;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.StringHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * dayPerformance collector
 */
public class PerfCollector {

    private static final Logger logger = Logger.getLogger(PerfCollector.class);

    private Map<String, StockVo> name2stock;    // stock name map to date index
    private StockVo marketIndex;
    private boolean isInTrain = false;

    private int cobRangeFrom, cobRangeTo;
    private double[] end, start, max, min;
    private int[] stockCob;

    private List<StockScore> allScoreRecords = new ArrayList<>();
    private Map<String, List<StockScore>> name2scores = new HashMap<>();
    private DescriptiveStatistics kellyGood = new DescriptiveStatistics();
    private DescriptiveStatistics kellyBad = new DescriptiveStatistics();

    private DescriptiveStatistics perfStats = new DescriptiveStatistics();
    private DescriptiveStatistics sharpeStats = new DescriptiveStatistics();
    private DescriptiveStatistics holdingDaysStats = new DescriptiveStatistics();
    private DayStatistics buyDayStatistics, sellDayStatistics;

    public TreeMap<Integer, List<StockScore>> cob2scoreList;
    public TreeMap<Integer, TopKHeap<StockScore>> cob2topK;
    public MidasTrainOptions options;

    public PerfCollector() {
    }

    public void ctor(Map<String, StockVo> name2stock, MidasTrainOptions options, int[] dates) throws MidasException {
        this.options = options;
        this.name2stock = name2stock;
        marketIndex = name2stock.get(MidasConstants.MARKET_INDEX_NAME);
        cobRangeTo = marketIndex.getEnd();
        buyDayStatistics = new DayStatistics();
        sellDayStatistics = new DayStatistics();

        if(options != null && dates != null){
            if(options.useSignal){
                cob2scoreList = new TreeMap<>();
                for(int cob : dates){
                    cob2scoreList.put(cob, new ArrayList<>());
                }
            } else {
                cob2topK = new TreeMap<>();
                for(int cob : dates){
                    cob2topK.put(cob, new TopKHeap<>(MidasConstants.SCORE_TOP_K, StockScore.class));
                }
            }
        }
        cobRangeFrom = 20140601;
        clear();
    }

    public void clear(){
        allScoreRecords.clear();
        ArrayHelper.clear(kellyGood, kellyBad, perfStats, sharpeStats, holdingDaysStats);
        buyDayStatistics.clear();
        sellDayStatistics.clear();
        if(cob2scoreList != null){
            cob2scoreList.values().forEach(List<StockScore>::clear);
        }
        if(cob2topK != null){
            cob2topK.values().forEach(TopKHeap::clear);
        }
    }

    public void addRecord(StockScoreRecord record) throws MidasException {
        recordByName(record);
        if(!isInTrain){
            allScoreRecords.addAll(record.getRecords());
        }

        if(record.getCob() >= cobRangeFrom && record.getCob() <= cobRangeTo){
            for(StockScore stockScore : record.getRecords()){
                if(stockScore.state == StockScoreState.Signaled || stockScore.getScore() <= 0.5) continue;
                initState(stockScore.getStockCode());
                recordBuySellDayStatistics(stockScore);

//                if(stockScore.getStockCode().equalsIgnoreCase("SZ002219")){
//                    logger.info("debug: " + stockCob[stockScore.buyIndex]);
//                }

                double totalChangePct, buyPrice, sellPrice;
                buyPrice = stockScore.buyTiming == 0 ? start[stockScore.buyIndex] : end[stockScore.buyIndex];
                sellPrice = stockScore.sellTiming == 0 ? start[stockScore.sellIndex] : end[stockScore.sellIndex];
                marketIndex.calculatePerformance(stockScore);
                totalChangePct = MathStockUtil.calculateChangePct(buyPrice, sellPrice);
                stockScore.setPerf(totalChangePct);
                stockScore.calculateDailyExcessReturn();
                sharpeStats.addValue(stockScore.dailyExcessReturn);
                perfStats.addValue(totalChangePct);
                holdingDaysStats.addValue(stockScore.holdingPeriod);

                if(totalChangePct < 0){
                    kellyBad.addValue(totalChangePct);
                } else {
                    kellyGood.addValue(totalChangePct);
                }
            }
        }
    }

    private void recordBuySellDayStatistics(StockScore stockScore){
        buyDayStatistics.recordStatistics(end[stockScore.buyIndex - 1], start[stockScore.buyIndex], end[stockScore.buyIndex], max[stockScore.buyIndex], min[stockScore.buyIndex]);
        sellDayStatistics.recordStatistics(end[stockScore.sellIndex - 1], start[stockScore.sellIndex], end[stockScore.sellIndex], max[stockScore.sellIndex], min[stockScore.sellIndex]);
    }

    private void initState(String stockCode) throws MidasException {
        StockVo stock = name2stock.get(stockCode);
        // in ScoreManager already advanced by 1. so this index of cob is the next day of signal
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        stockCob = stock.getDatesInt();
    }

    public double kellyFormula(){
        if(kellyBad.getN() + kellyGood.getN() > 0){
            double p = ((double)kellyBad.getN()) / (kellyBad.getN() + kellyGood.getN());
            double revenueWin = kellyGood.getMean(), revenueLoss = kellyBad.getMean();
            if(Math.abs(revenueLoss * revenueWin) > 1e-9){
                return (p * revenueWin + (1 - p) * revenueLoss) / revenueLoss * revenueWin;
            }
        }
        return 0d;
    }

    public double kellyAnnualizedPerformance(double kellyFraction){
        if(kellyFraction <= 0d) return 0d;
        double[] perf = perfStats.getValues();
        double[] period = holdingDaysStats.getValues();
        double result = 0d;
        if(perf.length > 0 && perf.length == period.length){
            for (int i = 0; i < perf.length; i++) {
                result += Math.pow(1d + kellyFraction * perf[i], 250d / period[i]);
            }
            return result / perf.length;
        }
        return result;
    }

    public SingleParameterTrainResult getResult(){
        SingleParameterTrainResult result = new SingleParameterTrainResult();
        result.cnt = perfStats.getN();
        result.dayPerformance = perfStats.getMean();
        result.stdDev = perfStats.getStandardDeviation();
        result.setBuyDayStatistics(buyDayStatistics);
        result.setSellDayStatistics(sellDayStatistics);
        result.holdingDays = holdingDaysStats.getMean();
        result.kellyFraction = kellyFormula();
        result.kellyAnnualizedPerformance = kellyAnnualizedPerformance(result.kellyFraction);
        result.sharpeRatio = Math.sqrt(250) * sharpeStats.getMean() / sharpeStats.getStandardDeviation();
        return result;
    }

    @Override
    public String toString() {
        return getResult().toString();
    }

    /**
     * this is for file output, include all score record sorting by performance desc, it is good for examine the worst case
     */
    public String toPerfString() {
        Collections.sort(allScoreRecords, new StockScoreComparator());
        return "PerfCollector {" +
                "\nallScoreRecords = " + StringHelper.toString(allScoreRecords) +
                getResult().toString() +
                "\n}";
    }

    public void setIsInTrain(boolean isInTrain) {
        this.isInTrain = isInTrain;
    }

    private void recordByName(StockScore score){
        if(name2scores.containsKey(score.getStockCode())){
            name2scores.get(score.getStockCode()).add(score);
        } else {
            List<StockScore> tmpScores = new ArrayList<>();
            tmpScores.add(score);
            name2scores.put(score.getStockCode(), tmpScores);
        }
    }

    private void recordByName(StockScoreRecord record){
        if(record != null && CollectionUtils.isNotEmpty(record.getRecords())){
            record.getRecords().forEach(this::recordByName);
        }
    }

    public Map<String, List<StockScore>> getName2scores() {
        return name2scores;
    }

    public List<List<StockScore>> getAllScoreListSortByCob(){
        List<List<StockScore>> list = new ArrayList<>();
        if(options.useSignal){
            list.addAll(cob2scoreList.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));
        } else {
            list.addAll(cob2topK.entrySet().stream().map(entry -> entry.getValue().toList()).collect(Collectors.toList()));
        }
        return list;
    }
}
