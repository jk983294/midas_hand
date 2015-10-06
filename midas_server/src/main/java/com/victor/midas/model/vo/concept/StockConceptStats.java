package com.victor.midas.model.vo.concept;

import com.victor.midas.model.vo.StockVo;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * stock concept
 */
public class StockConceptStats implements Comparable<StockConceptStats> {

    private StockConcept concept;

    private Set<StockVo> stocks;

    private DescriptiveStatistics scoreStatistics = new DescriptiveStatistics();

    private double score;

    public StockConceptStats(StockConcept concept) {
        this.concept = concept;
        stocks = new HashSet<>();
    }

    public void addStock(StockVo stockVo){
        stocks.add(stockVo);
    }

    public StockConcept getConcept() {
        return concept;
    }

    public void setConcept(StockConcept concept) {
        this.concept = concept;
    }

    public Set<StockVo> getStocks() {
        return stocks;
    }

    public void setStocks(Set<StockVo> stocks) {
        this.stocks = stocks;
    }

    public DescriptiveStatistics getScoreStatistics() {
        return scoreStatistics;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "StockConceptStats{" +
                "concept=" + concept +
                ", stocks=" + stocks +
                '}';
    }

    @Override
    public int compareTo(StockConceptStats o) {
        return Double.compare(score, o.getScore());
    }

    public static class ComparatorStockCnt implements Comparator {
        public int compare(Object arg0, Object arg1) {
            StockConceptStats conceptStats0 = (StockConceptStats)arg0;
            StockConceptStats conceptStats1 = (StockConceptStats)arg1;
            return Integer.compare(conceptStats0.getStocks().size(), conceptStats1.getStocks().size());
        }
    }
}
