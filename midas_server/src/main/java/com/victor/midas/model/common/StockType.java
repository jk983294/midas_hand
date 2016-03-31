package com.victor.midas.model.common;

/**
 * type for stock
 */
public enum StockType {
    Index,
    SZ,
    SH,
    FundA,
    FundB,
    UNKNOWN;


    @Override
    public String toString() {
        return super.toString();
    }

    public static StockType getStockType(String prefix){
        if("IDX".equalsIgnoreCase(prefix)){
            return Index;
        } else if("SZ".equalsIgnoreCase(prefix)){
            return SZ;
        } else if("SH".equalsIgnoreCase(prefix)){
            return SH;
        } else {
            return UNKNOWN;
        }
    }
}
