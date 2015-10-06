package com.victor.midas.model.train;

/**
 * used to record trade
 */
public class TradeRecordSimple {

    private String stockName;

    private int date;

    private int count;

    private double price;

    private boolean isBuy;

    private StockDecision reason;

    public TradeRecordSimple() {
    }

    public TradeRecordSimple(String stockName, int date, int count, double price, boolean isBuy, StockDecision reason) {
        this.stockName = stockName;
        this.date = date;
        this.count = count;
        this.price = price;
        this.isBuy = isBuy;
        this.reason = reason;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public StockDecision getReason() {
        return reason;
    }

    public void setReason(StockDecision reason) {
        this.reason = reason;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
