package com.victor.midas.model.common;


import java.util.List;

public enum MarketDataType {
    stock,
    fund,
    bond,
    tushare_report;

    public static MarketDataType getDataType(MarketDataType defaultPara, List<String> params, int index){
        if(params != null && params.size() > index){
            return MarketDataType.valueOf(params.get(index));
        } else {
            return defaultPara;
        }
    }
}
