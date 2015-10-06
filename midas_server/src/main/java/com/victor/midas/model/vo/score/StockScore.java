package com.victor.midas.model.vo.score;


/**
 * represent a score
 */
public class StockScore implements Comparable<StockScore> {

    private String stockCode;

    private String conceptName;

    private double score;

    public StockScore() {
    }

    public StockScore(String stockCode, double score) {
        this.stockCode = stockCode;
        this.score = score;
    }

    public StockScore(String stockCode, String conceptName, double score) {
        this.stockCode = stockCode;
        this.conceptName = conceptName;
        this.score = score;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(StockScore o) {
        return (Double.valueOf(score)).compareTo(o.getScore());
    }

    @Override
    public String toString() {
        return "StockScore{" +
                "stockCode='" + stockCode + '\'' +
                ", conceptName='" + conceptName + '\'' +
                ", score=" + score +
                '}';
    }
}
