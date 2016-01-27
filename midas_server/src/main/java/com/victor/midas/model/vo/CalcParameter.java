package com.victor.midas.model.vo;

import java.util.Arrays;
import java.util.Collections;

/**
 * used to store calc parameter
 */
public class CalcParameter implements Cloneable {

    /** time frame for price moving average*/
    public int priceMaShort, priceMaMedium, priceMaMonth, priceMaLong, priceMaYear;
    /** time frame for volume moving average*/
    public int volumeMaShort, volumeMaMedium, volumeMaLong, volumeMaYear;
    /** threshold for determine tangle state*/
    public double bullLine, bearLine;
    /** train related */
    public int trainStartDate, trainEndDate, backTestStartDate, backTestEndDate;
    /** single parameter train related */
    public int singleInt = 1;
    public double singleDouble = 1d;
    /** due to incomplete data, first several days is not valid*/
    public int exceptionDays;

    public double tradeTaxRate;

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
}
