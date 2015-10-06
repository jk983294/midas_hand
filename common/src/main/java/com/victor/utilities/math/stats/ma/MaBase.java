package com.victor.utilities.math.stats.ma;

/**
 * interface for Moving average
 */
public interface MaBase {

    public double[] calculate(double[] data, int interval);

    /**
     * result[0 - oldLength] is calculated, caluclate the rest result, data.length > oldresult.length
     */
    public double[] calculate(double[] data, double[] oldResult, int interval);

    /** result is pre-allocated, used to store result*/
    public void calculateInPlace(double[] data, int interval, double[] result);

}
