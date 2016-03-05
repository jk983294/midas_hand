package com.victor.midas.train.common;

public class MidasTrainOptions {

    public boolean isInTrain = false;
    /**
     * buy signal means when score is > 1d
     * quit signal means when score is < -1d, then it will sell stock
     * if useSignal is false, means the sell decision will be controlled by PerfCollector
     */
    public boolean useSignal = true;

    public boolean selectTops = true;
}
