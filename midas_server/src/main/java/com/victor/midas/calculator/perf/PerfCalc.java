package com.victor.midas.calculator.perf;


import com.victor.midas.model.db.StockInfoDb;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * used to calculate virtual stock exchange for Dan
 */
public class PerfCalc {

    private static final Logger logger = Logger.getLogger(PerfCalc.class);

    private Map<String, Integer> stock2hand = new HashMap<>();

    private List<StockInfoDb> stockInfos;

    private static final double initFund = 10000d;
    private Double fund;

    public PerfCalc(List<StockInfoDb> stockInfos) {
        this.stockInfos = stockInfos;
    }

    public void calculate() throws IOException {
        List<String> contents = FileUtils.readLines(new File("D:\\MktData\\RawData\\dan stock performance.txt"), "GBK");
        String stockCode, action;
        int date;
        int hand;
        double price;
        fund = initFund;
        for (int i = 1; i < contents.size(); i++) {
            String arrs[] = contents.get(i).split("\t");
            date = Integer.valueOf(arrs[0]);
            stockCode = arrs[1];
            price = Double.valueOf(arrs[2]);
            action = arrs[3];
            hand = Integer.valueOf(arrs[4]);

            if("buy".equalsIgnoreCase(action)){
                if(fund > buyMoney(price, hand)){
                    fund -= buyMoney(price, hand);
                    addStockBuyRecord(stockCode, hand);
                } else {
                    logger.error("could not buy at " + price + " for " + stockCode + " at day " + date);
                }
            } else {
                if(couldSell(stockCode, hand)){
                    fund += sellMoney(price, hand);
                    reduceStockBuyRecord(stockCode, hand);
                } else {
                    logger.error("could not sell at " + price + " for " + stockCode + " at day " + date);
                }
            }
        }
    }

    private void addStockBuyRecord(String stockCode, int hand){
        if(stock2hand.get(stockCode) != null){
            stock2hand.put(stockCode, stock2hand.get(stockCode) + hand);
        } else {
            stock2hand.put(stockCode, hand);
        }
    }

    private void reduceStockBuyRecord(String stockCode, int hand){
        if(stock2hand.get(stockCode) - hand > 0){
            stock2hand.put(stockCode, stock2hand.get(stockCode) - hand);
        } else {
            stock2hand.remove(stockCode);
        }
    }

    private boolean couldSell(String stockCode, int hand){
        return stock2hand.get(stockCode) != null && stock2hand.get(stockCode) >= hand;
    }

    private static final double buyTax = 0.0003;
    private double buyMoney(double price, double hand){
        return price * hand * 100 * ( 1 + buyTax);
    }

    private static final double sellTax = 0.0013;
    private double sellMoney(double price, double hand){
        return price * hand * 100 * ( 1 - sellTax);
    }

    private double getAllMoney(){
        double money = fund;
        HashMap<String, StockInfoDb> infos = new HashMap<>();
        for(StockInfoDb info : stockInfos){
            infos.put(info.getName(), info);
        }
        for(String stockCode : stock2hand.keySet()){
            money += (infos.get(stockCode).getEnd()) * stock2hand.get(stockCode) * 100;
        }
        return money;
    }

    public Map<String, Object> getResult(){
        Map<String, Object> result = new HashMap<>();
        result.put("performance", getAllMoney() / initFund - 1d);
        result.put("remain money", fund);
        result.put("remain", stock2hand);
        return result;
    }
}