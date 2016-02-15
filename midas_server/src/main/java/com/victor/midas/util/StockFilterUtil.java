package com.victor.midas.util;

import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.StockVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filter out some data for calculation
 */
public class StockFilterUtil {

    private List<StockVo> allStockVos;
    private List<StockVo> tradableStocks = new ArrayList<>();
    private List<StockVo> indexStocks = new ArrayList<>();
    private Map<String, StockVo> name2stock = new HashMap<>();
    private StockVo marketIndex;
    private boolean needSmallSetForTest = false;

    public StockFilterUtil(List<StockVo> allStockVos) {
        this.allStockVos = allStockVos;
    }

    public void filter() throws MidasException {
        for(StockVo stock : allStockVos){
            name2stock.put(stock.getStockName(), stock);
            if(stock.getStockType() != StockType.Index){
                if(!needSmallSetForTest || (needSmallSetForTest && tradableStocks.size() < 10))
                    tradableStocks.add(stock);
            } else if(stock.getStockType() == StockType.Index){
                indexStocks.add(stock);
                if(MidasConstants.MARKET_INDEX_NAME.equalsIgnoreCase(stock.getStockName())){
                    marketIndex = stock;
                }
            }
        }
        if(marketIndex == null){
            throw new MidasException("no market index is found.");
        }
    }

    public List<StockVo> getTradableStocks() {
        return tradableStocks;
    }

    public List<StockVo> getIndexStocks() {
        return indexStocks;
    }

    public StockVo getMarketIndex() {
        return marketIndex;
    }

    public Map<String, StockVo> getName2stock() {
        return name2stock;
    }

    public List<StockVo> getAllStockVos() {
        return allStockVos;
    }
}
