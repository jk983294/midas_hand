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

    private int cobRangeFrom, cobRangeTo;
    private double[] changePct, end, start, max, min;

    private List<StockScore> upsets = new ArrayList<>();
    private DescriptiveStatistics kellyGood = new DescriptiveStatistics();
    private DescriptiveStatistics kellyBad = new DescriptiveStatistics();

    private DescriptiveStatistics perfStats = new DescriptiveStatistics();
    private DescriptiveStatistics openStatsDay1 = new DescriptiveStatistics();
    private DescriptiveStatistics closeStatsDay1 = new DescriptiveStatistics();
    private DescriptiveStatistics highStatsDay1 = new DescriptiveStatistics();
    private DescriptiveStatistics lowStatsDay1 = new DescriptiveStatistics();
    private DescriptiveStatistics openStatsDay2 = new DescriptiveStatistics();
    private DescriptiveStatistics closeStatsDay2 = new DescriptiveStatistics();
    private DescriptiveStatistics highStatsDay2 = new DescriptiveStatistics();
    private DescriptiveStatistics lowStatsDay2 = new DescriptiveStatistics();

    public PerfCollector(Map<String, StockVo> name2stock) {
        this.name2stock = name2stock;
        cobRangeTo = name2stock.get(MidasConstants.MARKET_INDEX_NAME).getEnd();
        clear();
    }

    public void clear(){
        cobRangeFrom = 20140601;
        upsets.clear();
        ArrayHelper.clear(kellyGood, kellyBad, perfStats, openStatsDay1, closeStatsDay1, highStatsDay1, lowStatsDay1, openStatsDay2, closeStatsDay2, highStatsDay2, lowStatsDay2);
    }

    public void addRecord(StockScoreRecord record) throws MidasException {
        double totalChangePct = 0d;
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

                totalChangePct = 0d;
                // first day, buy from beginning, earning is the difference
                if(index < stock.getDatesInt().length){
                    totalChangePct += MathStockUtil.calculateChangePct(start[index], end[index]);
                    openStatsDay1.addValue(MathStockUtil.calculateChangePct(end[index - 1], start[index]));
                    closeStatsDay1.addValue(MathStockUtil.calculateChangePct(end[index - 1], end[index]));
                    highStatsDay1.addValue(MathStockUtil.calculateChangePct(end[index - 1], max[index]));
                    lowStatsDay1.addValue(MathStockUtil.calculateChangePct(end[index - 1], min[index]));
                    //addPerf(MathStockUtil.calculateChangePct(start[index], end[index]));
                }
                // second, sell at end
                if(index + 1 < stock.getDatesInt().length){
                    totalChangePct += changePct[index + 1];
                    openStatsDay2.addValue(MathStockUtil.calculateChangePct(end[index], start[index + 1]));
                    closeStatsDay2.addValue(MathStockUtil.calculateChangePct(end[index], end[index + 1]));
                    highStatsDay2.addValue(MathStockUtil.calculateChangePct(end[index], max[index + 1]));
                    lowStatsDay2.addValue(MathStockUtil.calculateChangePct(end[index], min[index + 1]));
                    //addPerf(changePct[index + 1]);
                }
                if(index < stock.getDatesInt().length){
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
        result.d1Open = openStatsDay1.getMean();
        result.d1OpenStdDev = openStatsDay1.getStandardDeviation();
        result.d1Close = closeStatsDay1.getMean();
        result.d1CloseStdDev = closeStatsDay1.getStandardDeviation();
        result.d1High = highStatsDay1.getMean();
        result.d1HighStdDev = highStatsDay1.getStandardDeviation();
        result.d1Low = lowStatsDay1.getMean();
        result.d1LowStdDev = lowStatsDay1.getStandardDeviation();
        result.d2Open = openStatsDay2.getMean();
        result.d2OpenStdDev = openStatsDay2.getStandardDeviation();
        result.d2Close = closeStatsDay2.getMean();
        result.d2CloseStdDev = closeStatsDay2.getStandardDeviation();
        result.d2High = highStatsDay2.getMean();
        result.d2HighStdDev = highStatsDay2.getStandardDeviation();
        result.d2Low = lowStatsDay2.getMean();
        result.d2LowStdDev = lowStatsDay2.getStandardDeviation();
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
