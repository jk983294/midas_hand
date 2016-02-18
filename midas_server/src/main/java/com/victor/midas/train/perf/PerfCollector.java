package com.victor.midas.train.perf;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

/**
 * dayPerformance collector
 */
public class PerfCollector {

    private Map<String, StockVo> name2stock;    // stock name map to date index
    private String stockCode;
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

    private int cobRangeFrom, cobRangeTo;
    private double[] changePct, end, start, max, min;

    private List<StockScore> upsets = new ArrayList<>();
    private DescriptiveStatistics kellyGood = new DescriptiveStatistics();
    private DescriptiveStatistics kellyBad = new DescriptiveStatistics();

    private DescriptiveStatistics perfStats = new DescriptiveStatistics();
    private DayStatistics[] dayStatisticses;

    public PerfCollector(Map<String, StockVo> name2stock) throws MidasException {
        this.name2stock = name2stock;
        cobRangeTo = name2stock.get(MidasConstants.MARKET_INDEX_NAME).getEnd();
        holdDays = (holdHalfDays + buyTiming) / 2 + 1;
        if(holdDays < 2) throw new MidasException("T + 1, so holding day should big than 1");
        isSellAtOpen = (holdHalfDays + buyTiming) % 2 == 0;
        isBuyAtOpen = buyTiming % 2 == 0;
        dayStatisticses = new DayStatistics[holdDays];
        for (int i = 0; i < holdDays; i++) {
            dayStatisticses[i] = new DayStatistics();
        }
        clear();
    }

    public void clear(){
        cobRangeFrom = 20140601;
        upsets.clear();
        ArrayHelper.clear(kellyGood, kellyBad, perfStats);
        for (int i = 0; i < holdDays; i++) {
            dayStatisticses[i].clear();
        }
    }

    public void addRecord(StockScoreRecord record) throws MidasException {
        double totalChangePct, buyPrice, sellPrice;
        if(record.getCob() >= cobRangeFrom && record.getCob() <= cobRangeTo){
            for(StockScore stockScore : record.getRecords()){
                if(stockScore.getScore() <= 0.5) continue;
                stockCode = stockScore.getStockCode();
                StockVo stock = name2stock.get(stockCode);
                index = stock.getCobIndex();
                changePct = (double[])stock.queryCmpIndex("changePct");
                end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
                start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
                max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
                min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);

                int dateCnt = stock.getDatesInt().length;
                for (int i = 0; i < holdDays; i++) {
                    if(index + i < dateCnt){
                        dayStatisticses[i].recordStatistics(end[index + i - 1], start[index + i], end[index + i], max[index + i], min[index + i]);
                    }
                }
                if(index < dateCnt){
                    buyPrice = isBuyAtOpen ? start[index] : end[index];
                    if(index + holdDays - 1 < dateCnt){
                        sellPrice = isSellAtOpen ? start[index + holdDays - 1] : end[index + holdDays - 1];
                    } else {    // last day's close liquidate out
                        sellPrice = end[dateCnt - 1];
                    }
                    totalChangePct = MathStockUtil.calculateChangePct(buyPrice, sellPrice);
                    stockScore.setPerf(totalChangePct);
                    perfStats.addValue(totalChangePct);
                    if(!isInTrain) upsets.add(stockScore);

                    if(totalChangePct < 0){
                        kellyBad.addValue(totalChangePct);
                    } else {
                        kellyGood.addValue(totalChangePct);
                    }
                }
            }
        }
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
        result.d1Open = dayStatisticses[0].openStats.getMean();
        result.d1OpenStdDev = dayStatisticses[0].openStats.getStandardDeviation();
        result.d1Close = dayStatisticses[0].closeStats.getMean();
        result.d1CloseStdDev = dayStatisticses[0].closeStats.getStandardDeviation();
        result.d1High = dayStatisticses[0].highStats.getMean();
        result.d1HighStdDev = dayStatisticses[0].highStats.getStandardDeviation();
        result.d1Low = dayStatisticses[0].lowStats.getMean();
        result.d1LowStdDev = dayStatisticses[0].lowStats.getStandardDeviation();
        result.d2Open = dayStatisticses[holdDays - 1].openStats.getMean();
        result.d2OpenStdDev = dayStatisticses[holdDays - 1].openStats.getStandardDeviation();
        result.d2Close = dayStatisticses[holdDays - 1].closeStats.getMean();
        result.d2CloseStdDev = dayStatisticses[holdDays - 1].closeStats.getStandardDeviation();
        result.d2High = dayStatisticses[holdDays - 1].highStats.getMean();
        result.d2HighStdDev = dayStatisticses[holdDays - 1].highStats.getStandardDeviation();
        result.d2Low = dayStatisticses[holdDays - 1].lowStats.getMean();
        result.d2LowStdDev = dayStatisticses[holdDays - 1].lowStats.getStandardDeviation();
        result.kellyFraction = kellyFormula();
        result.kellyAnnualizedPerformance = kellyAnnualizedPerformance(result.kellyFraction, result.dayPerformance);
        return result;
    }



    private StockScoreComparator cmp = new StockScoreComparator();
    public void sortUpsets(){
        Collections.sort(upsets, cmp);
    }

    @Override
    public String toString() {
        sortUpsets();
        return getResult().toString();
    }

    public String toPerfString() {
        sortUpsets();
        return "PerfCollector {" +
                "\nupsets = " + upsetsToString() +
                getResult().toString() +
                "\n}";
    }

    private String upsetsToString(){
        StringBuilder sb = new StringBuilder();
        for(StockScore stockScore : upsets){
            sb.append(stockScore.toString()).append("\n");
        }
        return sb.toString();
    }

    private class StockScoreComparator implements Comparator<StockScore> {
        @Override
        public int compare(StockScore o1, StockScore o2) {
            if(o1.getPerf() > o2.getPerf()) return 1;
            else if(o1.getPerf() < o2.getPerf()) return -1;
            else return 0;
        }
    }

    public void setIsInTrain(boolean isInTrain) {
        this.isInTrain = isInTrain;
    }
}
