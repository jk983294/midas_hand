package com.victor.midas.model.vo;


import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

public class StockReport {
    @Id
    public String stockName;

    public Set<String> reports = new HashSet<>();

    public StockReport(String stockName) {
        this.stockName = stockName;
    }

    public StockReport() {
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Set<String> getReports() {
        return reports;
    }

    public void setReports(Set<String> reports) {
        this.reports = reports;
    }
}
