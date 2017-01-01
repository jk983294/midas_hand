package com.victor.midas.calculator.score.week;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.TimeHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * help to generate weekly data
 */
public class WeeklyDataUtil {

    public double[] min, max, end, start, volume, total;
    public int[] dates;
    public Date[] datesDate;
    public WeeklyStockData currentWeeklyData;
    public List<WeeklyStockData> weeks;
    public int len;
    public double[] ma5, ma10, ma20, ma30;
    private DescriptiveStatistics vMa5Stats = new DescriptiveStatistics(5);
    private DescriptiveStatistics ma5Stats = new DescriptiveStatistics(5);
    private DescriptiveStatistics ma10Stats = new DescriptiveStatistics(10);
    private DescriptiveStatistics ma20Stats = new DescriptiveStatistics(20);
    private DescriptiveStatistics ma30Stats = new DescriptiveStatistics(30);

    public void init(int len, double[] min, double[] max, double[] start, double[] end,
                     double[] volume, double[] total, int[] dates, Date[] datesDate){
        this.len = len;
        this.min = min;
        this.max = max;
        this.end = end;
        this.start = start;
        this.volume = volume;
        this.total = total;
        this.dates = dates;
        this.datesDate = datesDate;
        currentWeeklyData = null;
        weeks = new ArrayList<>();
        ma5 = new double[len];
        ma10 = new double[len];
        ma20 = new double[len];
        ma30 = new double[len];
        ArrayHelper.clear(vMa5Stats, ma5Stats, ma10Stats, ma20Stats, ma30Stats);
    }

    public void calculate(){
        for (int itr = 0; itr < len; itr++) {
            update(itr);
        }

        updateWeeklyVolumeMA5();

        calculateChangePct();

    }

    private void calculateChangePct(){
        if(CollectionUtils.isNotEmpty(weeks)){
            WeeklyStockData previous;
            currentWeeklyData = weeks.get(0);
            currentWeeklyData.changePct = MathStockUtil.calculateChangePct(currentWeeklyData.start, currentWeeklyData.end);
            currentWeeklyData.middleEntityPct = Math.abs(currentWeeklyData.changePct);
            currentWeeklyData.upEntityPct = MathStockUtil.calculateChangePct(Math.max(currentWeeklyData.end, currentWeeklyData.start), currentWeeklyData.max);
            currentWeeklyData.downEntityPct = MathStockUtil.calculateChangePct(currentWeeklyData.min, Math.min(currentWeeklyData.end, currentWeeklyData.start));
            currentWeeklyData.weekIndex = 0;
            for (int i = 1; i < weeks.size(); i++) {
                previous = currentWeeklyData;
                currentWeeklyData = weeks.get(i);
                currentWeeklyData.changePct = MathStockUtil.calculateChangePct(previous.end, currentWeeklyData.end);
                currentWeeklyData.middleEntityPct = Math.abs(MathStockUtil.calculateChangePct(currentWeeklyData.start, currentWeeklyData.end));
                currentWeeklyData.upEntityPct = MathStockUtil.calculateChangePct(Math.max(currentWeeklyData.end, currentWeeklyData.start), currentWeeklyData.max);
                currentWeeklyData.downEntityPct = MathStockUtil.calculateChangePct(currentWeeklyData.min, Math.min(currentWeeklyData.end, currentWeeklyData.start));
                currentWeeklyData.weekIndex = i;
            }
        }
    }

    private void update(int i){
        if(currentWeeklyData == null){
            currentWeeklyData = new WeeklyStockData(max[i], min[i], start[i], end[i], volume[i], total[i], dates[i], i, datesDate[i]);
            weeks.add(currentWeeklyData);
            addMaStats();
        } else {
            DateTime today = new DateTime(datesDate[i]);
            if(TimeHelper.isSameWeek(today, currentWeeklyData.lastDay)){
                currentWeeklyData.update(max[i], min[i], end[i], volume[i], total[i], dates[i], i, today);
                updateStats();
            } else {
                // update weekly volume MA5
                updateWeeklyVolumeMA5();

                // create new weekly data
                currentWeeklyData = new WeeklyStockData(max[i], min[i], start[i], end[i], volume[i], total[i], dates[i], i, datesDate[i]);
                weeks.add(currentWeeklyData);
                addMaStats();
            }
        }

        recordData(i);
    }

    private void updateWeeklyVolumeMA5(){
        vMa5Stats.addValue(currentWeeklyData.avgVolume);
        currentWeeklyData.vMa5 = vMa5Stats.getMean();
    }

    private void recordData(int i){
        ma5[i] = ma5Stats.getMean();
        ma10[i] = ma10Stats.getMean();
        ma20[i] = ma20Stats.getMean();
        ma30[i] = ma30Stats.getMean();
    }

    private void addMaStats(){
        ma5Stats.addValue(currentWeeklyData.end);
        ma10Stats.addValue(currentWeeklyData.end);
        ma20Stats.addValue(currentWeeklyData.end);
        ma30Stats.addValue(currentWeeklyData.end);
    }

    private void updateStats(){
        ma5Stats.replaceMostRecentValue(currentWeeklyData.end);
        ma10Stats.replaceMostRecentValue(currentWeeklyData.end);
        ma20Stats.replaceMostRecentValue(currentWeeklyData.end);
        ma30Stats.replaceMostRecentValue(currentWeeklyData.end);
    }

    /**
     * calculate average volume from the begin of the week until today index
     */
    public double getAvgVolumeOfWeek4ThatDay(WeeklyStockData weeklyStockData, int index){
        double avg = 0d;
        for(int i = weeklyStockData.cobFromIndex; i <= index; ++i){
            avg += volume[i];
        }
        return avg / (index - weeklyStockData.cobFromIndex + 1);
    }
}
