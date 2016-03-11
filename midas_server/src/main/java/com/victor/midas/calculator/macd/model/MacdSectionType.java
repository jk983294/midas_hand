package com.victor.midas.calculator.macd.model;


public enum MacdSectionType {
    red,
    green,
    unknown;

    public static MacdSectionType getType(double value){
        if(value <= 0d) return green;
        else return red;
    }
}
