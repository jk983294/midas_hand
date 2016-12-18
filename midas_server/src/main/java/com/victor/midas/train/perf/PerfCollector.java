package com.victor.midas.train.perf;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.BinarySearch;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.StringHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

/**
 * dayPerformance collector
 */
public class PerfCollector {

    private Map<String, StockVo> name2stock;    // stock name map to date index
    private StockVo marketIndex;
    private int index;                          // benchmark stock's date index
    private boolean isInTrain = false;
    /**
     * buyTiming = 1 holdHalfDays = 1 means buy at 1st day's close, sell at 2nd day's open
     * buyTiming = 0 holdHalfDays = 2 means buy at 1st day's open, sell at 2nd day's open
     * buyTiming = 0 holdHalfDays = 3 means buy at 1st day's open, sell at 2nd day's close
     */
    public int holdHalfDays = 3;                // 1 day contains 2 half day
    public int buyTiming = 0;                   // 0 open 1 close
    private int holdDays = 1;
    private boolean isSellAtOpen = false;
    private boolean isBuyAtOpen = true;

    private int cobRangeFrom, cobRangeTo, dateCnt;
    private double[] changePct, end, start, max, min;
    private int[] stockDates;

    private List<StockScore> allScoreRecords = new ArrayList<>();
    private Map<String, List<StockScore>> name2scores = new HashMap<>();
    private DescriptiveStatistics kellyGood = new DescriptiveStatistics();
    private DescriptiveStatistics kellyBad = new DescriptiveStatistics();

    private DescriptiveStatistics perfStats = new DescriptiveStatistics();
    private DescriptiveStatistics sharpeStats = new DescriptiveStatistics();
    private DescriptiveStatistics holdingDaysStats = new DescriptiveStatistics();
    private DayStatistics buyDayStatistics, sellDayStatistics;

    public PerfCollector(Map<String, StockVo> name2stock) throws MidasException {
        this.name2stock = name2stock;
        marketIndex = name2stock.get(MidasConstants.MARKET_INDEX_NAME);
        cobRangeTo = marketIndex.getEnd();
        holdDays = (holdHalfDays + buyTiming) / 2 + 1;
        if(holdDays < 2) throw new MidasException("T + 1, so holding day should big than 1");
        isSellAtOpen = (holdHalfDays + buyTiming) % 2 == 0;
        isBuyAtOpen = buyTiming % 2 == 0;
        buyDayStatistics = new DayStatistics();
        sellDayStatistics = new DayStatistics();
        clear();
    }

    public PerfCollector() {
    }

    public void clear(){
        cobRangeFrom = 20140601;
        allScoreRecords.clear();
        ArrayHelper.clear(kellyGood, kellyBad, perfStats, sharpeStats, holdingDaysStats);
        buyDayStatistics.clear();
        sellDayStatistics.clear();
    }

    public void addRecord(StockScoreRecord record) throws MidasException {
        recordByName(record);
        if(record.getCob() >= cobRangeFrom && record.getCob() <= cobRangeTo){
            for(StockScore stockScore : record.getRecords()){
                if(stockScore.getScore() <= 0.5 || stockScore.sellIndex <= 0) continue;
                initState(stockScore.getStockCode());
                recordBuySellDayStatistics();
                if(index < dateCnt){
                    if(stockScore.holdingPeriod == -1){     // controlled by PerfCollector
                        recordCollectorControlledScore(stockScore);
                    } else {                                // controlled by quit signal
                        recordStrategyControlledScore(stockScore);
                    }
                }
            }
        }
    }

    /**
     * controlled by quit signal
     * @throws MidasException
     */
    private void recordStrategyControlledScore(StockScore stockScore) throws MidasException {
        double totalChangePct, buyPrice, sellPrice;
        buyPrice = stockScore.buyTiming == 0 ? start[stockScore.buyIndex] : end[stockScore.buyIndex];
        sellPrice = stockScore.sellTiming == 0 ? start[stockScore.sellIndex] : end[stockScore.sellIndex];
        marketIndex.calculatePerformance(stockScore);
        totalChangePct = MathStockUtil.calculateChangePct(buyPrice, sellPrice);
        stockScore.setPerf(totalChangePct);
        stockScore.calculateDailyExcessReturn();
        sharpeStats.addValue(stockScore.dailyExcessReturn);
        perfStats.addValue(totalChangePct);
        if(stockScore.holdingPeriod > 0) holdingDaysStats.addValue(stockScore.holdingPeriod);
        if(!isInTrain) allScoreRecords.add(stockScore);

        if(totalChangePct < 0){
            kellyBad.addValue(totalChangePct);
        } else {
            kellyGood.addValue(totalChangePct);
        }
    }

    /**
     * controlled by PerfCollector
     * @throws MidasException
     */
    private void recordCollectorControlledScore(StockScore stockScore) throws MidasException {
        double totalChangePct, buyPrice, sellPrice;
        buyPrice = isBuyAtOpen ? start[index] : end[index];
        stockScore.buyCob = stockDates[index];
        stockScore.buyTiming = buyTiming;
        if(index + holdDays - 1 < dateCnt){
            sellPrice = isSellAtOpen ? start[index + holdDays - 1] : end[index + holdDays - 1];
            stockScore.sellCob = stockDates[index + holdDays - 1];
            stockScore.sellTiming = isSellAtOpen ? 0 : 1;
        } else {    // last day's close liquidate out
            sellPrice = end[dateCnt - 1];
            stockScore.sellCob = stockDates[dateCnt - 1];
            stockScore.sellTiming = 1;
        }
        marketIndex.calculatePerformance(stockScore);
        totalChangePct = MathStockUtil.calculateChangePct(buyPrice, sellPrice);
        stockScore.setPerf(totalChangePct);
        stockScore.calculateDailyExcessReturn();
        sharpeStats.addValue(stockScore.dailyExcessReturn);
        perfStats.addValue(totalChangePct);
        if(stockScore.holdingPeriod > 0) holdingDaysStats.addValue(stockScore.holdingPeriod);
        if(!isInTrain) allScoreRecords.add(stockScore);

        if(totalChangePct < 0){
            kellyBad.addValue(totalChangePct);
        } else {
            kellyGood.addValue(totalChangePct);
        }
    }

    private void recordBuySellDayStatistics(){
        if(index < dateCnt){
            buyDayStatistics.recordStatistics(end[index - 1], start[index], end[index], max[index], min[index]);
        }
        if(index + holdDays - 1 < dateCnt){
            sellDayStatistics.recordStatistics(end[index + holdDays - 2], start[index + holdDays - 1], end[index + holdDays - 1], max[index + holdDays - 1], min[index + holdDays - 1]);
        }
    }

    private void initState(String stockCode) throws MidasException {
        StockVo stock = name2stock.get(stockCode);
        index = stock.getCobIndex();
        changePct = (double[])stock.queryCmpIndex("changePct");
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        stockDates = stock.getDatesInt();
        dateCnt = stock.getDatesInt().length;
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

    public double kellyAnnualizedPerformance(double kellyFraction, double expectedDayPerformance){
        return Math.pow(1d + kellyFraction * expectedDayPerformance, 250d);
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
        result.kellyAnnualizedPerformance = kellyAnnualizedPerformance(result.kellyFraction, result.dayPerformance);
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
            for(StockScore stockScore : record.getRecords()){
                recordByName(stockScore);
            }
        }
    }

    public Map<String, List<StockScore>> getName2scores() {
        return name2scores;
    }
}
