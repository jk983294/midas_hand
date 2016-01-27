package com.victor.midas.model.vo.score;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * represent selected StockScore for one cob date
 */
public class StockScoreRecord {

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
}
