package com.victor.midas.model.vo.concept;

import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

/**
 * stock concept
 */
public class StockCrawlData {
    @Id
    private String stockCode;

    private Set<StockConcept> concepts;

    public StockCrawlData(String stockCode) {
        this.stockCode = stockCode;
        concepts = new HashSet<>();
    }

    public void addConcept(StockConcept stockConcept){
        if(!concepts.contains(stockConcept)){
            concepts.add(stockConcept);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockCrawlData that = (StockCrawlData) o;

        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return stockCode != null ? stockCode.hashCode() : 0;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Set<StockConcept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<StockConcept> concepts) {
        this.concepts = concepts;
    }
}
