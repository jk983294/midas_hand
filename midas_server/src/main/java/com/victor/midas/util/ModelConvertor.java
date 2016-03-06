package com.victor.midas.util;

import com.victor.midas.model.db.*;
import com.victor.midas.model.db.misc.StockNamesDb;
import com.victor.midas.model.train.StockTrain;
import com.victor.midas.model.vo.StockVo;

import java.util.*;

/**
 * convert models
 */
public class ModelConvertor {

    private static Set<String> indexNames;

    static {
        indexNames = new HashSet<>();
        indexNames.add(MidasConstants.INDEX_NAME_START);
        indexNames.add(MidasConstants.INDEX_NAME_MAX);
        indexNames.add(MidasConstants.INDEX_NAME_MIN);
        indexNames.add(MidasConstants.INDEX_NAME_END);
        indexNames.add(MidasConstants.INDEX_NAME_VOLUME);
        indexNames.add(MidasConstants.INDEX_NAME_TOTAL);
        indexNames.add(MidasConstants.INDEX_NAME_CHANGEPCT);
        indexNames.add("prod");
    }

    public static StockNamesDb convert2StockNames(List<StockVo> stocks){
        List<String> stockNames = new ArrayList<>();
        for (StockVo stock : stocks){
            stockNames.add(stock.getStockName());
        }
        return new StockNamesDb(MidasConstants.MISC_ALL_STOCK_NAMES, stockNames);
    }

    public static List<StockInfoDb> convert2StockInfo(List<StockVo> stocks) throws MidasException {
        List<StockInfoDb> stockInfoDbs = new ArrayList<>();
        for (StockVo stock : stocks){
            StockInfoDb stockInfo = new StockInfoDb();
            stockInfo.setName(stock.getStockName());
            stockInfo.setDesp(stock.getDesp());
            stockInfo.setDate(stock.getEnd());
            stockInfo.setStart(stock.getStart());
            stockInfo.setMax(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_MAX));
            stockInfo.setMin(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_MIN));
            stockInfo.setEnd(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_END));
            stockInfo.setVolume(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_VOLUME));
            stockInfo.setTotal(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_TOTAL));
            stockInfo.setChange(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_CHANGEPCT));

            stockInfoDbs.add(stockInfo);
        }
        return stockInfoDbs;
    }

    public static Map<String, StockVo> toStockMap(List<StockVo> stocks){
        Map<String, StockVo> map = new HashMap<>();
        for(StockVo stock : stocks){
            map.put(stock.getStockName(), stock);
        }
        return map;
    }

    /**
     * this is for big data set, each time calculation done, remove some unused index to save space
     * @param stockTrain
     * @throws MidasException
     */
    public static void removeUnnecessaryIndex(StockTrain stockTrain) throws MidasException {
        StockVo oldStock = stockTrain.getStock();

        Map<String, int[]> indexInts = oldStock.getIndexInts();
        Set<String> toRemove = new HashSet<>();
        for(String indexName : indexInts.keySet()){
            if(!indexNames.contains(indexName)){
                toRemove.add(indexName);
            }
        }
        for(String indexName : toRemove){
            indexInts.remove(indexName);
        }

        toRemove.clear();
        Map<String, double[]> indexDoubles = oldStock.getIndexDoubles();
        for(String indexName : indexDoubles.keySet()){
            if(!indexNames.contains(indexName)){
                toRemove.add(indexName);
            }
        }
        for(String indexName : toRemove){
            indexDoubles.remove(indexName);
        }
    }

    public static String getTimingString(int timing){
        return timing == 0 ? "open" : "close";
    }

}
