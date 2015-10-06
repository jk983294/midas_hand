package com.victor.midas.model.vo;

import java.util.Arrays;
import java.util.Collections;

/**
 * used to store calc parameter
 */
public class CalcParameter implements Cloneable {

    /** time frame for price moving average*/
    private int priceMaShort;
    private int priceMaMedium;
    private int priceMaMonth;
    private int priceMaLong;
    private int priceMaYear;

    /** time frame for volume moving average*/
    private int volumeMaShort;
    private int volumeMaMedium;
    private int volumeMaLong;
    private int volumeMaYear;

    /** threshold for determine tangle state*/
    private double bullLine;
    private double bearLine;

    /** train related */
    private int trainStartDate;
    private int trainEndDate;
    private int backTestStartDate;
    private int backTestEndDate;

    /** due to incomplete data, first several days is not valid*/
    private int exceptionDays;

    private double tradeTaxRate;

    // set default value for all parameters
    public CalcParameter() {
        priceMaShort = 5;
        priceMaMedium = 10;
        priceMaMonth = 30;
        priceMaLong = 60;
        priceMaYear = 250;

        volumeMaShort = 5;
        volumeMaMedium = 10;
        volumeMaLong = 30;
        volumeMaYear = 250;

        bullLine = -0.002;
        bearLine = 0.002;

        trainStartDate = 20050101;
        trainEndDate = 20141107;
        backTestStartDate = 20110101;
        backTestEndDate = 20141107;

        tradeTaxRate = 0.003;

        exceptionDays = Collections.max(Arrays.asList(priceMaShort, priceMaMedium));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public double getBullLine() {
        return bullLine;
    }

    public void setBullLine(double bullLine) {
        this.bullLine = bullLine;
    }

    public double getBearLine() {
        return bearLine;
    }

    public void setBearLine(double bearLine) {
        this.bearLine = bearLine;
    }

    public int getPriceMaShort() {
        return priceMaShort;
    }

    public void setPriceMaShort(int priceMaShort) {
        this.priceMaShort = priceMaShort;
    }

    public int getPriceMaMedium() {
        return priceMaMedium;
    }

    public void setPriceMaMedium(int priceMaMedium) {
        this.priceMaMedium = priceMaMedium;
    }

    public int getPriceMaLong() {
        return priceMaLong;
    }

    public void setPriceMaLong(int priceMaLong) {
        this.priceMaLong = priceMaLong;
    }

    public int getPriceMaYear() {
        return priceMaYear;
    }

    public void setPriceMaYear(int priceMaYear) {
        this.priceMaYear = priceMaYear;
    }

    public int getVolumeMaShort() {
        return volumeMaShort;
    }

    public void setVolumeMaShort(int volumeMaShort) {
        this.volumeMaShort = volumeMaShort;
    }

    public int getVolumeMaMedium() {
        return volumeMaMedium;
    }

    public void setVolumeMaMedium(int volumeMaMedium) {
        this.volumeMaMedium = volumeMaMedium;
    }

    public int getVolumeMaLong() {
        return volumeMaLong;
    }

    public void setVolumeMaLong(int volumeMaLong) {
        this.volumeMaLong = volumeMaLong;
    }

    public int getVolumeMaYear() {
        return volumeMaYear;
    }

    public void setVolumeMaYear(int volumeMaYear) {
        this.volumeMaYear = volumeMaYear;
    }

    public int getTrainStartDate() {
        return trainStartDate;
    }

    public void setTrainStartDate(int trainStartDate) {
        this.trainStartDate = trainStartDate;
    }

    public int getTrainEndDate() {
        return trainEndDate;
    }

    public void setTrainEndDate(int trainEndDate) {
        this.trainEndDate = trainEndDate;
    }

    public int getBackTestStartDate() {
        return backTestStartDate;
    }

    public void setBackTestStartDate(int backTestStartDate) {
        this.backTestStartDate = backTestStartDate;
    }

    public int getBackTestEndDate() {
        return backTestEndDate;
    }

    public void setBackTestEndDate(int backTestEndDate) {
        this.backTestEndDate = backTestEndDate;
    }

    public int getExceptionDays() {
        return exceptionDays;
    }

    public void setExceptionDays(int exceptionDays) {
        this.exceptionDays = exceptionDays;
    }

    public double getTradeTaxRate() {
        return tradeTaxRate;
    }

    public void setTradeTaxRate(double tradeTaxRate) {
        this.tradeTaxRate = tradeTaxRate;
    }

    public int getPriceMaMonth() {
        return priceMaMonth;
    }

    public void setPriceMaMonth(int priceMaMonth) {
        this.priceMaMonth = priceMaMonth;
    }
}
