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

    public List<KeyValue<Double, String>> upPct = new ArrayList<>();
    public List<KeyValue<Double, String>> downPct = new ArrayList<>();

    public StockDayStats(int cob) {
        this.cob = cob;
    }

    public int getCob() {
        return cob;
    }

    public void setCob(int cob) {
        this.cob = cob;
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

    @Override
    public String toString() {
        return "StockDayStats{" +
                "cob=" + cob +
                ", upPct=" + upPct +
                '}';
    }
}
