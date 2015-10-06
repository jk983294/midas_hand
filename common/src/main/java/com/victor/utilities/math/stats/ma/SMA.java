package com.victor.utilities.math.stats.ma;

import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * simple moving average (SMA) is the unweighted mean of the previous n data.
 */
public class SMA implements MaBase{

    @Override
    public double[] calculate(double[] data, int interval) {
        double[] result = new double[data.length];
        DescriptiveStatistics descriptivestats = new DescriptiveStatistics();
        descriptivestats.setWindowSize(interval);
        for( int i = 0; i < data.length; i++) {
            descriptivestats.addValue(data[i]);
            result[i] = descriptivestats.getMean();
        }
        return result;
    }


    @Override
    public double[] calculate(double[] data, double[] oldResult, int interval) {
        if(ArrayHelper.isNull(oldResult) || oldResult.length < interval + 2){
            return calculate(data, interval);
        }
        double[] newResult = ArrayHelper.copyToNewLenArray(oldResult, data.length);
        DescriptiveStatistics descriptiveStats = new DescriptiveStatistics();
        descriptiveStats.setWindowSize(interval);
        for( int i = Math.max(0, oldResult.length - interval); i < oldResult.length; i++) {
            descriptiveStats.addValue(data[i]);
            newResult[i] = descriptiveStats.getMean();
        }
        for( int i = oldResult.length; i < newResult.length; i++) {
            descriptiveStats.addValue(data[i]);
            newResult[i] = descriptiveStats.getMean();
        }
        return newResult;
    }

    @Override
    public void calculateInPlace(double[] data, int interval, double[] result) {
        DescriptiveStatistics descriptivestats = new DescriptiveStatistics();
        descriptivestats.setWindowSize(interval);
        for( int i = 0; i < data.length; i++) {
            descriptivestats.addValue(data[i]);
            result[i] = descriptivestats.getMean();
        }
    }
}
