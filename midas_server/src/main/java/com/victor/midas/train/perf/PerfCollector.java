package com.victor.midas.train.perf;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Map;

/**
 * performance collector
 */
public class PerfCollector {

    private Map<String, StockVo> name2stock;    // stock name map to date index
    private String stockCode;
    private int index;                          // benchmark stock's date index

    private int cobRangeFrom, cobRangeTo;
    private double[] changePct, end, start;

    private double perf;
    private int cnt;
    private DescriptiveStatistics stats = new DescriptiveStatistics();

    public PerfCollector(Map<String, StockVo> name2stock) {
        this.name2stock = name2stock;
        cobRangeFrom = 20140601;
        cobRangeTo = name2stock.get(MidasConstants.SH_INDEX_NAME).getEnd();
        perf = 0d;
        cnt = 0;
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

                totalChangePct = 0d;
                // first day, buy from beginning, earning is the difference
                if(index < stock.getDatesInt().length){
                    totalChangePct += MathStockUtil.calculateChangePct(start[index], end[index]);
                    //addPerf(MathStockUtil.calculateChangePct(start[index], end[index]));
                }
                // second, sell at end
                if(index + 1 < stock.getDatesInt().length){
                    totalChangePct += changePct[index + 1];
                    //addPerf(changePct[index + 1]);
                }
                if(index < stock.getDatesInt().length){
                    addPerf(totalChangePct);
                }
            }
        }
    }

    private void addPerf(double pct){
        perf += pct;
        ++cnt;
        stats.addValue(pct);
    }

    @Override
    public String toString() {
        return "PerfCollector{" +
                "perf=" + stats.getSum() +
                ", cnt=" + stats.getN() +
                ", real perf=" + stats.getMean() +
                ", std dev=" + stats.getStandardDeviation() +
                '}';
    }
}
