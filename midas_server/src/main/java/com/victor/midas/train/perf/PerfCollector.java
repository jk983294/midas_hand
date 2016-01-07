package com.victor.midas.train.perf;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

/**
 * performance collector
 */
public class PerfCollector {

    private Map<String, StockVo> name2stock;    // stock name map to date index
    private String stockCode;
    private int index;                          // benchmark stock's date index

    private int cobRangeFrom, cobRangeTo;
    private double[] changePct, end, start, max, min;

    private List<StockScore> upsets = new ArrayList<>();

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
        cobRangeFrom = 20140601;
        cobRangeTo = name2stock.get(MidasConstants.SH_INDEX_NAME).getEnd();
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
                    upsets.add(stockScore);
//                    if(totalChangePct < 0){
//                        upsets.add(stockScore);
//                    }
                }
            }
        }
    }

    private StockScoreComparator cmp = new StockScoreComparator();
    public void sortUpsets(){
        Collections.sort(upsets, cmp);
    }

    @Override
    public String toString() {
        sortUpsets();
        return "PerfCollector{" +
                "\nperf=" + perfStats.getSum() +
                ", cnt=" + perfStats.getN() +
                ", real perf=" + perfStats.getMean() +
                ", std dev=" + perfStats.getStandardDeviation() +
                "\nday1 stats: open = " + openStatsDay1.getMean() + ", " + openStatsDay1.getStandardDeviation() +
                " close = " + closeStatsDay1.getMean() + ", " + closeStatsDay1.getStandardDeviation() +
                " high = " + highStatsDay1.getMean() + ", " + highStatsDay1.getStandardDeviation() +
                " low = " + lowStatsDay1.getMean() + ", " + lowStatsDay1.getStandardDeviation() +
                "\nday2 stats: open = " + openStatsDay2.getMean() + ", " + openStatsDay2.getStandardDeviation() +
                " close = " + closeStatsDay2.getMean() + ", " + closeStatsDay2.getStandardDeviation() +
                " high = " + highStatsDay2.getMean() + ", " + highStatsDay2.getStandardDeviation() +
                " low = " + lowStatsDay2.getMean() + ", " + lowStatsDay2.getStandardDeviation() +
                '}';
    }

    public String toPerfString() {
        sortUpsets();
        return "PerfCollector{" +
                "\nupsets = " + upsetsToString() +
                "\nperf=" + perfStats.getSum() +
                ", cnt=" + perfStats.getN() +
                ", real perf=" + perfStats.getMean() +
                ", std dev=" + perfStats.getStandardDeviation() +
                "\nday1 stats: open = " + openStatsDay1.getMean() + ", " + openStatsDay1.getStandardDeviation() +
                " close = " + closeStatsDay1.getMean() + ", " + closeStatsDay1.getStandardDeviation() +
                " high = " + highStatsDay1.getMean() + ", " + highStatsDay1.getStandardDeviation() +
                " low = " + lowStatsDay1.getMean() + ", " + lowStatsDay1.getStandardDeviation() +
                "\nday2 stats: open = " + openStatsDay2.getMean() + ", " + openStatsDay2.getStandardDeviation() +
                " close = " + closeStatsDay2.getMean() + ", " + closeStatsDay2.getStandardDeviation() +
                " high = " + highStatsDay2.getMean() + ", " + highStatsDay2.getStandardDeviation() +
                " low = " + lowStatsDay2.getMean() + ", " + lowStatsDay2.getStandardDeviation() +
                '}';
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
}
