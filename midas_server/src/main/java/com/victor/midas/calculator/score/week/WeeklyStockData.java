package com.victor.midas.calculator.score.week;

import com.victor.utilities.utils.gson.Ignore;
import org.joda.time.DateTime;

import java.util.Date;

public class WeeklyStockData {

    public double max, min, start, end, volume, total;
    public int cobFrom, cobTo, cobFromIndex, cobToIndex, weekIndex;
    public double avgVolume;                // since some week doesn't have 5 days records, so use this average volume instead for comparison
    public double vMa5;
    public double changePct, middleEntityPct, upEntityPct, downEntityPct;
    /**
     * if end > max MA, then 5; if end > second max MA, then 4, etc.
     * [1, 5]
     */
    public int orderOfPriceMa;
    /**
     * if short term MA > long term MA, then maScore++
     * the more bull market, the high maScore
     * [0, 6]
     */
    public int maScore;
    /**
     * the count of weeks which end price is above max MA, i.e. orderOfPriceMa = 5
     * we tolerate one week is below max MA
     */
    public int aboveMaxMaWeekCount;
    /**
     * the count of ma[i] < ma[i - 1]
     */
    public int maSlopeDownCount;

    @Ignore
    public DateTime lastDay;                // used for same week check

    public WeeklyStockData() {
    }

    public WeeklyStockData(double max, double min, double start, double end,
                           double volume, double total, int cob, int cobIndex, Date date) {
        this.max = max;
        this.min = min;
        this.start = start;
        this.end = end;
        this.avgVolume = this.volume = volume;
        this.total = total;
        this.cobFrom = this.cobTo = cob;
        this.cobFromIndex = this.cobToIndex = cobIndex;
        this.lastDay = new DateTime(date);
    }

    public void update(double max, double min, double end,
                       double volume, double total, int cob, int cobIndex, DateTime today) {
        this.max = Math.max(max, this.max);
        this.min = Math.min(min, this.min);
        this.end = end;
        this.volume += volume;
        this.total += total;
        this.cobTo = cob;
        this.cobToIndex = cobIndex;
        this.lastDay = today;
        this.avgVolume = this.volume / (cobToIndex - cobFromIndex + 1);
    }

    public boolean isLastDayOfTheWeek(int itr){
        return itr == cobToIndex;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public int getCobFrom() {
        return cobFrom;
    }

    public void setCobFrom(int cobFrom) {
        this.cobFrom = cobFrom;
    }

    public int getCobTo() {
        return cobTo;
    }

    public void setCobTo(int cobTo) {
        this.cobTo = cobTo;
    }

    public DateTime getLastDay() {
        return lastDay;
    }

    public void setLastDay(DateTime lastDay) {
        this.lastDay = lastDay;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCobFromIndex() {
        return cobFromIndex;
    }

    public void setCobFromIndex(int cobFromIndex) {
        this.cobFromIndex = cobFromIndex;
    }

    public int getCobToIndex() {
        return cobToIndex;
    }

    public void setCobToIndex(int cobToIndex) {
        this.cobToIndex = cobToIndex;
    }

    public double getAvgVolume() {
        return avgVolume;
    }

    public void setAvgVolume(double avgVolume) {
        this.avgVolume = avgVolume;
    }

    public double getvMa5() {
        return vMa5;
    }

    public void setvMa5(double vMa5) {
        this.vMa5 = vMa5;
    }

    public double getChangePct() {
        return changePct;
    }

    public void setChangePct(double changePct) {
        this.changePct = changePct;
    }

    public int getOrderOfPriceMa() {
        return orderOfPriceMa;
    }

    public void setOrderOfPriceMa(int orderOfPriceMa) {
        this.orderOfPriceMa = orderOfPriceMa;
    }

    public int getMaScore() {
        return maScore;
    }

    public void setMaScore(int maScore) {
        this.maScore = maScore;
    }

    public int getWeekIndex() {
        return weekIndex;
    }

    public void setWeekIndex(int weekIndex) {
        this.weekIndex = weekIndex;
    }

    public double getMiddleEntityPct() {
        return middleEntityPct;
    }

    public void setMiddleEntityPct(double middleEntityPct) {
        this.middleEntityPct = middleEntityPct;
    }

    public double getUpEntityPct() {
        return upEntityPct;
    }

    public void setUpEntityPct(double upEntityPct) {
        this.upEntityPct = upEntityPct;
    }

    public double getDownEntityPct() {
        return downEntityPct;
    }

    public void setDownEntityPct(double downEntityPct) {
        this.downEntityPct = downEntityPct;
    }

    public int getAboveMaxMaWeekCount() {
        return aboveMaxMaWeekCount;
    }

    public void setAboveMaxMaWeekCount(int aboveMaxMaWeekCount) {
        this.aboveMaxMaWeekCount = aboveMaxMaWeekCount;
    }

    @Override
    public String toString() {
        return "WeeklyStockData{" +
                "max=" + max +
                ", min=" + min +
                ", start=" + start +
                ", end=" + end +
                ", volume=" + volume +
                ", total=" + total +
                ", cobFrom=" + cobFrom +
                ", cobTo=" + cobTo +
                ", cobFromIndex=" + cobFromIndex +
                ", cobToIndex=" + cobToIndex +
                ", avgVolume=" + avgVolume +
                ", vMa5=" + vMa5 +
                ", lastDay=" + lastDay +
                '}';
    }
}
