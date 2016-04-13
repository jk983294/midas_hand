package com.victor.midas.model.vo.score;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * represent selected StockScore for one cob date
 */
public class StockScoreRecord implements Comparable<StockScoreRecord> {

    @Id
    private int cob;

    private StockSeverity severity = StockSeverity.Normal;

    private List<StockScore> records;

    public StockScoreRecord(int cob, List<StockScore> records) {
        this.cob = cob;
        this.records = records;
    }

    public int getCob() {
        return cob;
    }

    public void setCob(int cob) {
        this.cob = cob;
    }

    public List<StockScore> getRecords() {
        return records;
    }

    public void setRecords(List<StockScore> records) {
        this.records = records;
    }

    public StockSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(StockSeverity severity) {
        this.severity = severity;
    }

    @Override
    public int compareTo(StockScoreRecord o) {
        return Integer.valueOf(o.cob).compareTo(cob);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockScoreRecord record = (StockScoreRecord) o;

        return cob == record.cob;

    }

    @Override
    public int hashCode() {
        return cob;
    }

    @Override
    public String toString() {
        return "StockScoreRecord{" +
                "cob=" + cob +
                ", severity=" + severity +
                ", records=" + records +
                '}';
    }
}
