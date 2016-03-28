package com.victor.midas.calculator.util;

import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * math calculation utils
 */
public class MathStockUtil {

    /**
     * calculate percentage
     */
    public static double calculateChangePct(double yesterday, double today){
        return (today - yesterday) / yesterday;
    }

    /**
     * calculate percentage
     */
    public static double calculateChangePctAbs(double yesterday, double today){
        return Math.abs((today - yesterday) / yesterday);
    }

    /**
     * usage :
     * offset = 0, x = min[], y = max[], result = amplitudeRatio
     * offset = 1, x = end[], y = end[], result = changePct
     */
    public static double[] calculateChangePct(double[] x, double[] y, int offset){
        int len = x.length;
        double[] result = new double[len];
        for (int i = offset; i < len; i++) {
            result[i] = (y[i] - x[i - offset]) / x[i - offset];
        }
        return result;
    }

    public static double calculatePct(double yesterday, double delta){
        return delta / yesterday;
    }

    public static double calculatePct(int sampleCount, int totalCount){
        return ((double) sampleCount) / totalCount;
    }

    public static double[] differencePct(double[] a, double[] b){
        if(ArrayHelper.isNull(a) || ArrayHelper.isNull(b)) return null;
        int length = a.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = (a[i] -b[i]) / b[i];
        }
        return result;
    }

    public static void differencePctInplace(double[] a, double[] b, double[] result){
        if(ArrayHelper.isNull(a) || ArrayHelper.isNull(b)) return;
        int length = a.length;
        for (int i = 0; i < length; i++) {
            result[i] = (a[i] -b[i]) / b[i];
        }
    }



    public final static int THRESHOLD_ABOVE = 1;
    public final static int THRESHOLD_UNDER = -THRESHOLD_ABOVE;
    public final static int THRESHOLD_IN_RANGE = 1;
    public final static int THRESHOLD_NOT_IN_RANGE = -THRESHOLD_IN_RANGE;

    /**
     * calculate threshold vector
     */
    public static int[] thresholdCalc(double[] a, double threshold){
        if(ArrayHelper.isNull(a)) return null;
        int length = a.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = a[i] >= threshold ? THRESHOLD_ABOVE : THRESHOLD_UNDER;
        }
        return result;
    }

    public static int[] thresholdCalc(double[] a, double lowBound, double upBound){
        if(ArrayHelper.isNull(a)) return null;
        int length = a.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = a[i] > lowBound && a[i] < upBound ? THRESHOLD_IN_RANGE : THRESHOLD_NOT_IN_RANGE;
        }
        return result;
    }

    public static int[] thresholdVectorRevert(int[] threshold){
        if(ArrayHelper.isNull(threshold)) return null;
        int length = threshold.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = - threshold[i];
        }
        return result;
    }

    public static void thresholdCalcInplace(double[] a, double threshold, int[] result){
        if(ArrayHelper.isNull(a)) return;
        int length = a.length;
        for (int i = 0; i < length; i++) {
            result[i] = a[i] >= threshold ? THRESHOLD_ABOVE : THRESHOLD_UNDER;
        }
    }

    public static void thresholdCalcInplace(double[] a, double lowBound, double upBound, int[] result){
        if(ArrayHelper.isNull(a)) return;
        int length = a.length;
        for (int i = 0; i < length; i++) {
            result[i] = a[i] > lowBound && a[i] < upBound ? THRESHOLD_IN_RANGE : THRESHOLD_NOT_IN_RANGE;
        }
    }

    public static void thresholdVectorRevertInplace(int[] threshold, int[] result){
        if(ArrayHelper.isNull(threshold)) return;
        int length = threshold.length;
        for (int i = 0; i < length; i++) {
            result[i] = - threshold[i];
        }
    }

    public static double averagePayoffs(double[] payoffs){
        double mean = StatUtils.mean(payoffs);
        double variance = StatUtils.variance(payoffs);
        return mean / variance;
    }

    /**
     * in case some stock ex right, so that the volume be twiced or more
     * use volume = total / price to give more accurate volume
     */
    public static double[] calcAvgVolume(double[] end, double[] start, double[] total){
        if(ArrayHelper.isNull(end)) return null;
        int length = end.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = total[i] * 2d /(start[i] + end[i]);
        }
        return result;
    }

    public static boolean isPriceStop(double changePct){
        return Math.abs(Math.abs(changePct) - 0.1) < 0.005;
    }

    public static boolean isCrossZero(double v1, double v2){
        return v1 * v2 < 0;
    }

    public static boolean isCrossZeroUp(double v1, double v2){
        return isCrossZero(v1, v2) && v2 > v1;
    }

    public static boolean isCrossZeroDown(double v1, double v2){
        return isCrossZero(v1, v2) && v2 < v1;
    }
}
