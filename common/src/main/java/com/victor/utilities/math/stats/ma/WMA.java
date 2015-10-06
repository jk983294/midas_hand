package com.victor.utilities.math.stats.ma;

import com.victor.utilities.utils.ArrayHelper;

/**
 * weighted moving average (WMA) has the specific meaning of weights that decrease in arithmetical progression
 */
public class WMA implements MaBase {

    @Override
    public double[] calculate(double[] data, int interval) {
        double[] result = new double[data.length];

        int length = 0;
        double total = 0,  numerator = 0;
        for( int i = 0; i < interval; ++i) {
            total += data[i];
            numerator +=  (i + 1) * data[i];
            length = denominator(i + 1);
            result[i] = numerator / length;
        }

        for( int i = interval, len = data.length; i < len; ++i) {
            numerator +=  interval * data[i] - total;
            total += ( data[i] - data[i - interval]);
            result[i] = numerator / length;
        }
        return result;
    }

    @Override
    public double[] calculate(double[] data, double[] oldResult, int interval) {
        if(ArrayHelper.isNull(oldResult) || oldResult.length < interval + 2){
            return calculate(data, interval);
        }

        double[] result = ArrayHelper.copyToNewLenArray(oldResult, data.length);

        int length = denominator(interval);
        double total = 0,  numerator = 0;
        for( int i = oldResult.length - interval ; i < oldResult.length; ++i) {
            total += data[i];
            numerator +=  (interval - (oldResult.length - i) + 1) * data[i];
        }

        for( int i = oldResult.length, len = data.length; i < len; ++i) {
            numerator +=  interval * data[i] - total;
            total += ( data[i] - data[i - interval]);
            result[i] = numerator / length;
        }
        return result;
    }

    @Override
    public void calculateInPlace(double[] data, int interval, double[] result) {
        int length = 0;
        double total = 0,  numerator = 0;
        for( int i = 0; i < interval; ++i) {
            total += data[i];
            numerator +=  (i + 1) * data[i];
            length = denominator(i + 1);
            result[i] = numerator / length;
        }

        for( int i = interval, len = data.length; i < len; ++i) {
            numerator +=  interval * data[i] - total;
            total += ( data[i] - data[i - interval]);
            result[i] = numerator / length;
        }
    }

    private static int denominator(int x){
        return (x + 1) * x / 2;
    }
}
