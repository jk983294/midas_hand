package com.victor.midas.model.vo;

import com.google.common.collect.ComparisonChain;
import com.victor.utilities.model.KeyValue;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * represent market stats for one cob date
 */
public class StockDayStats implements Comparable<StockDayStats> {

    @Id
    private int cob;

    public List<KeyValue<Double, String>> longTermUpPct = new ArrayList<>();
    public List<KeyValue<Double, String>> longTermDownPct = new ArrayList<>();
    public List<KeyValue<Double, String>> shortTermUpPct = new ArrayList<>();
    public List<KeyValue<Double, String>> shortTermDownPct = new ArrayList<>();
    public List<KeyValue<Double, String>> upSlow = new ArrayList<>();
    public List<KeyValue<Double, String>> downFast = new ArrayList<>();

    public StockDayStats(int cob) {
        this.cob = cob;
    }

    public int getCob() {
        return cob;
    }

    public void setCob(int cob) {
        this.cob = cob;
    }

    public List<KeyValue<Double, String>> getLongTermUpPct() {
        return longTermUpPct;
    }

    public void setLongTermUpPct(List<KeyValue<Double, String>> longTermUpPct) {
        this.longTermUpPct = longTermUpPct;
    }

    public List<KeyValue<Double, String>> getLongTermDownPct() {
        return longTermDownPct;
    }

    public void setLongTermDownPct(List<KeyValue<Double, String>> longTermDownPct) {
        this.longTermDownPct = longTermDownPct;
    }

    public List<KeyValue<Double, String>> getShortTermUpPct() {
        return shortTermUpPct;
    }

    public void setShortTermUpPct(List<KeyValue<Double, String>> shortTermUpPct) {
        this.shortTermUpPct = shortTermUpPct;
    }

    public List<KeyValue<Double, String>> getShortTermDownPct() {
        return shortTermDownPct;
    }

    public void setShortTermDownPct(List<KeyValue<Double, String>> shortTermDownPct) {
        this.shortTermDownPct = shortTermDownPct;
    }

    public List<KeyValue<Double, String>> getUpSlow() {
        return upSlow;
    }

    public void setUpSlow(List<KeyValue<Double, String>> upSlow) {
        this.upSlow = upSlow;
    }

    public List<KeyValue<Double, String>> getDownFast() {
        return downFast;
    }

    public void setDownFast(List<KeyValue<Double, String>> downFast) {
        this.downFast = downFast;
    }

    @Override
    public int compareTo(StockDayStats o) {
        return ComparisonChain.start().compare(o.cob, cob).result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockDayStats record = (StockDayStats) o;

        return cob == record.cob;

    }

    @Override
    public int hashCode() {
        return cob;
    }

}
