package com.victor.midas.train.common;

public class MidasTrainOptions {

    public boolean isInTrain = false;
    /**
     * buy signal means when score is > 1d
     * quit signal means when score is < -1d, then it will sell stock
     * if useSignal is false, means the sell decision will be controlled by PerfCollector
     */
    public boolean useSignal = false;

    public boolean selectTops = true;

    /**
     * default is buy open at next day of buy signal day, sell at close of sell signal day
     */
    public int buyTiming = 0, sellTiming = 1;
}
