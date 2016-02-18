package com.victor.midas.train.perf;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DayStatistics {

    public DescriptiveStatistics openStats = new DescriptiveStatistics();
    public DescriptiveStatistics closeStats = new DescriptiveStatistics();
    public DescriptiveStatistics highStats = new DescriptiveStatistics();
    public DescriptiveStatistics lowStats = new DescriptiveStatistics();

    public void recordStatistics(double yesterdayClose, double open, double close, double max, double min){
        openStats.addValue(MathStockUtil.calculateChangePct(yesterdayClose, open));
        closeStats.addValue(MathStockUtil.calculateChangePct(yesterdayClose, close));
        highStats.addValue(MathStockUtil.calculateChangePct(yesterdayClose, max));
        lowStats.addValue(MathStockUtil.calculateChangePct(yesterdayClose, min));
    }

    public void clear(){
        ArrayHelper.clear(openStats, closeStats, highStats, lowStats);
    }
}
