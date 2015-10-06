package com.victor.midas.train.common;

import com.victor.midas.model.train.StockDecision;
import com.victor.midas.model.train.StockTrain;
import com.victor.midas.model.train.TradeRecord;
import com.victor.midas.model.train.TradeRecordSimple;
import com.victor.midas.model.vo.TrainItem;
import com.victor.midas.util.MidasConstants;

import java.util.*;

/**
 * portfolio
 */
public class Portfolio {

    private double tradeTaxRate;

    private double position;

    private boolean isHistoryRecord;

    private List<TradeRecord> history;

    private List<TradeRecordSimple> historySimple;

    private Map<String, Integer> holding;

    private int opportunity, totalCount;
    private double r;
    private double winProbability;

    public Portfolio(){}
    public Portfolio(double tradeTaxRate) {
        this.tradeTaxRate = tradeTaxRate;
        position = MidasConstants.INITIATE_FUND;
        history = new ArrayList<>();
        historySimple = new ArrayList<>();
        holding = new HashMap<>();
        isHistoryRecord = false;
    }

    public boolean hasPositionOn(String stockCode){
        return holding.containsKey(stockCode) && holding.get(stockCode) > 0;
    }

    public void buy(String stockCode, int date, double price, StockDecision reason){
        int hand = Double.valueOf(position / ((1 + tradeTaxRate)* price * 100)).intValue();
        if(hand > 0){
            int count = hand * 100;
            position = position - count * (1 + tradeTaxRate) * price;
            addHolding(stockCode, count);
            TradeRecordSimple record = new TradeRecordSimple(stockCode, date, count, price, true, reason);
            historySimple.add(record);
        }
    }

    public void sell(String stockCode, int date, double price, StockDecision reason){
        if(hasPositionOn(stockCode)){
            int count = holding.get(stockCode);
            position = position + (1 - tradeTaxRate) * count * price;
            clearHolding(stockCode);
            TradeRecordSimple record = new TradeRecordSimple(stockCode, date, count, price, false, reason);
            historySimple.add(record);
        }
    }

    public void generateRecord(Map<String, StockTrain> stockMap) throws Exception {
        history.clear();
        Map<String, TradeRecord> stock2record = new HashMap<>();
        TradeRecord existRecord = null;
        String stockCode;
        for (int i = 0; i < historySimple.size(); ++i) {
            TradeRecordSimple record = historySimple.get(i);
            stockCode = record.getStockName();
            if(record.isBuy()){
                // has existing record, not first buy, then merge with previous buy data
                if(stock2record.containsKey(stockCode)){
                    existRecord = stock2record.get(stockCode);
                    double previousPrice = existRecord.getBuyPrice(), currentPrice = record.getPrice();
                    int previousCount = existRecord.getCount(), currentCount = record.getCount();
                    existRecord.setCount(previousCount + currentCount);
                    existRecord.setBuyPrice((previousPrice * previousCount + currentPrice * currentCount) / (previousCount + currentCount));
                    existRecord.addBuyReason(record.getReason());
                } else {    // no existing record, first buy, then set first buy data
                    stock2record.put(stockCode, new TradeRecord(stockCode, record.getDate(), record.getPrice(), record.getCount(), record.getReason()));
                }
            } else {    // sell logic, record to history
                if(stock2record.containsKey(stockCode)){
                    existRecord = stock2record.get(stockCode);
                    if(existRecord.getCount() == record.getCount()){    // exactly sell all
                        existRecord.setSellPrice(record.getPrice());
                        existRecord.setSellDate(record.getDate());
                        existRecord.setSellDecision(record.getReason());
                        addHistory(existRecord);
                        stock2record.remove(stockCode);
                    } else if(existRecord.getCount() > record.getCount()){  // sell partial stock
                        TradeRecord newExisting = (TradeRecord)existRecord.clone();
                        newExisting.setSellPrice(record.getPrice());
                        newExisting.setSellDate(record.getDate());
                        newExisting.setSellDecision(record.getReason());
                        newExisting.setCount(record.getCount());
                        addHistory(newExisting);
                        existRecord.setCount(existRecord.getCount() - record.getCount());
                    }
                }
            }
        }
        // if remain some record, that means hold some stock till end
        for(Map.Entry<String, TradeRecord> entry : stock2record.entrySet()){
            existRecord = entry.getValue();
            stockCode = entry.getKey();
            StockTrain stockRemain = stockMap.get(stockCode);
            existRecord.setSellPrice(stockRemain.getLatestEndPrice());
            existRecord.setSellDate(stockRemain.getLatestDate());
            existRecord.setSellDecision(StockDecision.SELL_FOR_STATISTICS);
            addHistory(existRecord);
        }
    }

    private void addHolding(String stockCode, int count){
        if(count > 0){
            if(hasPositionOn(stockCode)){
                holding.put(stockCode, holding.get(stockCode) + count);
            } else {
                holding.put(stockCode, count);
            }
        }
    }

    private void clearHolding(String stockCode){
        if(hasPositionOn(stockCode)){
            holding.remove(stockCode);
        }
    }

    public void init(){
        position = MidasConstants.INITIATE_FUND;
    }

    public double performance(){
        opportunity = 0;
        totalCount = history.size();
        r = 0;
        winProbability = 0;

        double winPct;
        int winCount = 0;
        for(TradeRecord record : history){
            winPct = record.getR();
            if(winPct > 0) ++winCount;
            r += winPct;
            ++opportunity;
        }
        winProbability = ((double) winCount) / totalCount;
        r /= totalCount;
        return  r * opportunity;
    }

    public TrainItem generateTrainResult(){
        TrainItem item = new TrainItem();
        item.setHistory(history);
        item.setR(r);
        item.setOpportunity(opportunity);
        item.setWinProbability(winProbability);
        item.setFitness(r * opportunity);
        return item;
    }

    protected void addHistory(StockTrain stock, double buyPrice, int buyDate, double sellPrice, int sellDate,
                              int count, StockDecision buyDecision, StockDecision sellDecision){
        TradeRecord record = new TradeRecord(stock.getStock().getStockName(), buyDate, sellDate, buyPrice,
                sellPrice, buyDecision, sellDecision, count);
        record.setR(sellPrice/buyPrice - 1.0);
        history.add(record);
    }

    protected void addHistory(TradeRecord record){
        record.setR(record.getSellPrice() / record.getBuyPrice() - 1.0 - tradeTaxRate);
        history.add(record);
    }

    public boolean isHistoryRecord() {
        return isHistoryRecord;
    }

    public void setHistoryRecord(boolean isHistoryRecord) {
        this.isHistoryRecord = isHistoryRecord;
    }

    public List<TradeRecord> getHistory() {
        return history;
    }

    public void setHistory(List<TradeRecord> history) {
        this.history = history;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }
}
