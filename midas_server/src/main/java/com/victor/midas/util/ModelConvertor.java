package com.victor.midas.util;

import com.victor.midas.model.db.*;
import com.victor.midas.model.db.misc.SampleCobDb;
import com.victor.midas.model.db.misc.StockNamesDb;
import com.victor.midas.model.vo.StockDayStats;
import com.victor.midas.model.vo.StockReport;
import com.victor.midas.model.vo.StockVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * convert models
 */
public class ModelConvertor {

    public static StockNamesDb convert2StockNames(List<StockVo> stocks){
        List<String> stockNames = stocks.stream().map(StockVo::getStockName).collect(Collectors.toList());
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
            stockInfo.setChange(stock.queryLastIndexDouble(MidasConstants.INDEX_NAME_CHANGE_PCT));

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

    public static Map<String, StockReport> toStockReportMap(List<StockReport> reports){
        Map<String, StockReport> map = new HashMap<>();
        if(CollectionUtils.isNotEmpty(reports)){
            for(StockReport report : reports){
                map.put(report.getStockName(), report);
            }
        }
        return map;
    }

    public static SampleCobDb extractCob(List<StockDayStats> samples){
        List<Integer> cobs = samples.stream().map(StockDayStats::getCob).collect(Collectors.toList());
        return new SampleCobDb(MidasConstants.MISC_STOCK_DAY_STATS_COBS, cobs);
    }

    public static String getTimingString(int timing){
        return timing == 0 ? "open" : "close";
    }

    public static int string2date(String str){
        int date = 0;
        date += Integer.valueOf(str.substring(0,4)) * 10000;
        date += Integer.valueOf(str.substring(5,7)) * 100;
        date += Integer.valueOf(str.substring(8,10));
        return date;
    }

}
