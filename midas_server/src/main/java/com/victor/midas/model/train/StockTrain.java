package com.victor.midas.model.train;


import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.BinarySearch;

import java.util.*;

/**
 * stock value object
 */
public class StockTrain {

    private StockVo stock;

    /*** int represented times */
    private int startIndex;

    private int endIndex;

    private int currentIndex;

    /*** next day's decision */
    private StockDecision decision;

    /*** times series */
    private int[] dates;

    private double[] open;
    private double[] end;

    /*** index Component Name -> indexData*/
    private Map<String, int[]> indexInts;

    private Map<String, double[]> indexDoubles;
    /*** index Name -> index Component Names List*/
    private Map<String, List<String>> indexName2indexCmp;

    public StockTrain(StockVo stock) throws MidasException {
        this.stock = stock;
        this.dates = stock.getDatesInt();
        this.indexInts = stock.getIndexInts();
        this.indexDoubles = stock.getIndexDoubles();
        this.indexName2indexCmp = stock.getIndexName2indexCmp();
        open = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        decision = StockDecision.WATCH;
    }

    public void initTrainDayIndex(int trainStartDate, int trainEndDate, int exceptionDays){
        startIndex = Math.abs(BinarySearch.find(trainStartDate, dates)) + exceptionDays;
        endIndex = Math.abs(BinarySearch.find(trainEndDate, dates));
    }

    public void reSetCurrentIndex(){
        currentIndex = startIndex;
        decision = StockDecision.WATCH;
    }

    public double getCurrentOpenPrice(){
        return open[currentIndex];
    }

    public double getCurrentEndPrice(){
        return end[currentIndex];
    }

    public double getLatestEndPrice(){
        return end[endIndex];
    }

    public int getLatestDate(){
        return dates[endIndex];
    }

    public int getCurrentDate() {
        return dates[currentIndex];
    }

    public int getDateByIndex(int index) {
        return dates[index];
    }

    /*** query data */
    public Object queryCmpIndex(String cmpName) throws MidasException {
        if(indexInts != null && indexInts.containsKey(cmpName)){
            return indexInts.get(cmpName);
        } else if(indexDoubles != null && indexDoubles.containsKey(cmpName)){
            return indexDoubles.get(cmpName);
        } else {
            throw new MidasException("no cmp index for " + stock.getStockName() + " query " + cmpName);
        }
    }

    public boolean isExistIndex(String indexName){
        return indexName2indexCmp.containsKey(indexName);
    }

    public Map<String, int[]> getIndexInts() {
        return indexInts;
    }

    public void setIndexInts(Map<String, int[]> indexInts) {
        this.indexInts = indexInts;
    }

    public Map<String, double[]> getIndexDoubles() {
        return indexDoubles;
    }

    public void setIndexDoubles(Map<String, double[]> indexDoubles) {
        this.indexDoubles = indexDoubles;
    }

    public Map<String, List<String>> getIndexName2indexCmp() {
        return indexName2indexCmp;
    }

    public void setIndexName2indexCmp(Map<String, List<String>> indexName2indexCmp) {
        this.indexName2indexCmp = indexName2indexCmp;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int[] getDates() {
        return dates;
    }

    public void setDates(int[] datesInt) {
        this.dates = datesInt;
    }

    public StockVo getStock() {
        return stock;
    }

    public void setStock(StockVo stock) {
        this.stock = stock;
    }

    public StockDecision getDecision() {
        return decision;
    }

    public void setDecision(StockDecision decision) {
        this.decision = decision;
    }
}
