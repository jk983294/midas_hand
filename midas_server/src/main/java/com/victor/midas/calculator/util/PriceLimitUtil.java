package com.victor.midas.calculator.util;

import com.victor.utilities.utils.MathHelper;

/**
 * calculate all price stops
 * 1 normal limit up
 * 2 tomb limit up
 * -1 normal limit down
 * -2 tomb limit down
 */
public class PriceLimitUtil {

    public static final double epsilon = 0.005;

    public double[] end, start, max, min, changePct;
    public int[] result;
    public int len;

    public PriceLimitUtil() {
    }

    public void init(double[] end, double[] start, double[] max, double[] min, double[] changePct){
        this.end = end;
        this.start = start;
        this.max = max;
        this.min = min;
        this.changePct = changePct;
        len = end.length;
        result = new int[len];
        calculate();
    }

    public void calculate(){
        for (int i = 11; i < len; i++) {
            if(changePct[i] > 0d && MathHelper.isEqual(end[i], max[i])){
                if(MathHelper.isEqual(changePct[i], 0.10, epsilon) ||
                        (MathHelper.isEqual(changePct[i], 0.05, epsilon) && isStLimit(i))){
                    if(MathHelper.isEqual(start[i], max[i])){
                        result[i] = 2;
                    } else {
                        result[i] = 1;
                    }
                }
            } else if(changePct[i] < 0d && MathHelper.isEqual(end[i], min[i])){
                if(MathHelper.isEqual(changePct[i], -0.10, epsilon) ||
                        (MathHelper.isEqual(changePct[i], -0.05, epsilon) && isStLimit(i))){
                    if(MathHelper.isEqual(start[i], min[i])){
                        result[i] = -2;
                    } else {
                        result[i] = -1;
                    }
                }
            }
        }
    }

    /**
     * check if this stock is ST, ST limit is 5%
     * if previous 10 days price no more than 5%, then consider it ST
     */
    private boolean isStLimit(int i){
        for (int j = i - 1; j >= i - 10; j--) {
            if(Math.abs(MathStockUtil.calculateChangePct(end[j - 1], max[j])) - 0.05 > epsilon
                    || Math.abs(MathStockUtil.calculateChangePct(end[j - 1], min[j])) - 0.05 > epsilon) return false;
        }
        return true;
    }
}
