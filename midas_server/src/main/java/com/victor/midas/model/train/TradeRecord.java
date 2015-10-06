package com.victor.midas.model.train;

/**
 * used to record trade
 */
public class TradeRecord implements Cloneable {

    private String stockName;

    private int buyDate;

    private int sellDate;

    private double buyPrice;

    private double sellPrice;

    private String buyReason;       // may have many incremental buy actions

    private StockDecision buyDecision, sellDecision;

    private int count;

    private double R;

    public TradeRecord() {
    }

    public TradeRecord(String stockName, int buyDate, double buyPrice, int count, StockDecision buyDecision) {
        this.stockName = stockName;
        this.buyDate = buyDate;
        this.buyPrice = buyPrice;
        this.buyDecision = buyDecision;
        this.count = count;
        buyReason = buyDecision.toString();
    }

    public TradeRecord(String stockName, int buyDate, int sellDate, double buyPrice, double sellPrice,
                       StockDecision buyDecision, StockDecision sellDecision, int count) {
        this.stockName = stockName;
        this.buyDate = buyDate;
        this.sellDate = sellDate;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyDecision = buyDecision;
        this.sellDecision = sellDecision;
        this.count = count;
    }

    public void addBuyReason(StockDecision decision){
        if(buyReason == null){
            buyReason =decision.toString();
        } else {
            StringBuilder sb = new StringBuilder(buyReason);
            sb.append(" | ").append(decision.toString());
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeRecord that = (TradeRecord) o;

        if (buyDate != that.buyDate) return false;
        if (Double.compare(that.buyPrice, buyPrice) != 0) return false;
        if (sellDate != that.sellDate) return false;
        if (Double.compare(that.sellPrice, sellPrice) != 0) return false;
        if (stockName != null ? !stockName.equals(that.stockName) : that.stockName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = stockName != null ? stockName.hashCode() : 0;
        result = 31 * result + buyDate;
        result = 31 * result + sellDate;
        temp = Double.doubleToLongBits(buyPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sellPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(int buyDate) {
        this.buyDate = buyDate;
    }

    public int getSellDate() {
        return sellDate;
    }

    public void setSellDate(int sellDate) {
        this.sellDate = sellDate;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }

    public StockDecision getBuyDecision() {
        return buyDecision;
    }

    public void setBuyDecision(StockDecision buyDecision) {
        this.buyDecision = buyDecision;
    }

    public StockDecision getSellDecision() {
        return sellDecision;
    }

    public void setSellDecision(StockDecision sellDecision) {
        this.sellDecision = sellDecision;
    }

    @Override
    public String toString() {
        return "TradeRecord{" +
                "stockName='" + stockName + '\'' +
                ", buyDate=" + buyDate +
                ", sellDate=" + sellDate +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", count=" + count +
                '}';
    }
}
