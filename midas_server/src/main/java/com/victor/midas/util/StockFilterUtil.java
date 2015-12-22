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
    private StockVo indexSH;

    public StockFilterUtil(List<StockVo> allStockVos) {
        this.allStockVos = allStockVos;
    }

    public void filter(){
        for(StockVo stock : allStockVos){
            name2stock.put(stock.getStockName(), stock);
            if(stock.getStockType() != StockType.Index){
                tradableStocks.add(stock);
            } else if(stock.getStockType() == StockType.Index){
                indexStocks.add(stock);
                if(MidasConstants.SH_INDEX_NAME.equalsIgnoreCase(stock.getStockName())){
                    indexSH = stock;
                }
            }
        }
    }

    public List<StockVo> getTradableStocks() {
        return tradableStocks;
    }

    public List<StockVo> getIndexStocks() {
        return indexStocks;
    }

    public StockVo getIndexSH() {
        return indexSH;
    }

    public Map<String, StockVo> getName2stock() {
        return name2stock;
    }

    public List<StockVo> getAllStockVos() {
        return allStockVos;
    }
}
