package com.victor.midas.model.vo;


import com.google.common.collect.ComparisonChain;
import com.victor.utilities.utils.MathHelper;
import com.victor.utilities.utils.TimeHelper;

public class AipResult implements Comparable<AipResult> {

    public String stockName;

    public int startCob, endCob, monthCount, stockCountTotal, performanceOrder;
    public double availableMoney, totalInvestedMoney;
    public double performanceTotal, performanceMonthly;

    public AipResult(String stockName) {
        this.stockName = stockName;
    }

    public void buyStock(double price, double monthlyInvestMoney, int cob){
        if(startCob <= 0){
            startCob = cob;
        }
        endCob = cob;
        totalInvestedMoney += monthlyInvestMoney;
        availableMoney += monthlyInvestMoney;
        int stockCount = Double.valueOf(Math.floor(availableMoney / (price * 100d))).intValue() * 100;
        stockCountTotal += stockCount;
        availableMoney -= stockCount * price;
    }

    public void calculatePerformance(double closePrice){
        double totalMoney = availableMoney + closePrice * stockCountTotal;
        if(!MathHelper.isZero(totalInvestedMoney)){
            performanceTotal = totalMoney / totalInvestedMoney - 1.0;
            monthCount = TimeHelper.cob2month(endCob) - TimeHelper.cob2month(startCob) + 1;
            performanceMonthly = performanceTotal / monthCount;
        }
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getStartCob() {
        return startCob;
    }

    public void setStartCob(int startCob) {
        this.startCob = startCob;
    }

    public int getEndCob() {
        return endCob;
    }

    public void setEndCob(int endCob) {
        this.endCob = endCob;
    }

    public int getStockCountTotal() {
        return stockCountTotal;
    }

    public void setStockCountTotal(int stockCountTotal) {
        this.stockCountTotal = stockCountTotal;
    }

    public double getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(double availableMoney) {
        this.availableMoney = availableMoney;
    }

    public double getTotalInvestedMoney() {
        return totalInvestedMoney;
    }

    public void setTotalInvestedMoney(double totalInvestedMoney) {
        this.totalInvestedMoney = totalInvestedMoney;
    }

    public double getPerformanceTotal() {
        return performanceTotal;
    }

    public void setPerformanceTotal(double performanceTotal) {
        this.performanceTotal = performanceTotal;
    }

    public double getPerformanceMonthly() {
        return performanceMonthly;
    }

    public void setPerformanceMonthly(double performanceMonthly) {
        this.performanceMonthly = performanceMonthly;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    public int getPerformanceOrder() {
        return performanceOrder;
    }

    public void setPerformanceOrder(int performanceOrder) {
        this.performanceOrder = performanceOrder;
    }

    @Override
    public int compareTo(AipResult o) {
        return ComparisonChain.start()
                .compare(o.performanceMonthly, performanceMonthly)
                .result();
    }
}
