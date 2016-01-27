package com.victor.utilities.math.stats.ma;

import com.victor.utilities.utils.ArrayHelper;

/**
 * exponential moving average (EMA) is a type of infinite impulse response filter that
 * applies weighting factors which decrease exponentially.
 */
public class EMA implements MaBase{

    private double getAlpha(int interval){
        if(interval > 3){
            return 2d / (interval + 1);
        }
        return 0.7;
    }

    @Override
    public double[] calculate(double[] data, double[] oldResult, int interval) {
        double alpha = getAlpha(interval);
        if(ArrayHelper.isNull(oldResult) || oldResult.length < interval + 2){
            return calculate(data, interval);
        }

        double[] result = ArrayHelper.copyToNewLenArray(oldResult, data.length);

        for( int i = oldResult.length, len = data.length; i < len; ++i) {
            result[i] = alpha * data[i] + (1 - alpha) * result[i-1];
        }

        return result;
    }

    @Override
    public void calculateInPlace(double[] data, int interval, double[] result) {
        double alpha = getAlpha(interval);
        result[0] = data[0];
        for( int i = 1; i < data.length; i++) {
            result[i] = alpha * data[i] + (1-alpha) * result[i-1];
        }
    }

    @Override
    public double[] calculate(double[] data, int interval) {
        double alpha = getAlpha(interval);
        double[] result = new double[data.length];
        result[0] = data[0];
        for( int i = 1; i < data.length; i++) {
            result[i] = alpha * data[i] + (1-alpha) * result[i-1];
        }
        return result;
    }
}
