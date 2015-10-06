package com.victor.midas.model.vo;

/**
 * represent the focused stock, why is focused, how good is it
 */
public class FocusStock {

    private String stockCode;

    private double score;

    private String reason;

    public FocusStock() {
    }

    public FocusStock(String stockCode, double score, String reason) {
        this.stockCode = stockCode;
        this.score = score;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
