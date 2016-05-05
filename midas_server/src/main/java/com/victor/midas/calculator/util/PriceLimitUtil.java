package com.victor.midas.calculator.util;

import com.victor.utilities.utils.MathHelper;

/**
 * calculate all price stops
 * 1 normal limit up
 * 2 jump limit up
 * 3 tomb limit up
 * 4 no change limit up
 * -1 normal limit down
 * -2 jump limit up
 * -3 tomb limit down
 * -4 no change limit up
 */
public class PriceLimitUtil {

    public static final double epsilon = 0.005;

    public double[] end, start, max, min, changePct;
    public int[] result;
    public int len, timeFrame = 10;

    public int normalUpCnt, jumpUpCnt, tombUpCnt, noChangeUpCnt, totalUpCnt;
    public int normalDownCnt, jumpDownCnt, tombDownCnt, noChangeDownCnt, totalDownCnt;

    public PriceLimitUtil(int timeFrame) {
        this.timeFrame = timeFrame;
    }

    public void updateStats(int i){
        normalUpCnt = tombUpCnt = noChangeUpCnt = jumpUpCnt=
                normalDownCnt = tombDownCnt = noChangeDownCnt = jumpDownCnt =
                        totalUpCnt = totalDownCnt = 0;
        for (int j = Math.max(i - timeFrame + 1, 0); j <= i; j++) {
            switch (result[j]){
                case -1 : ++normalDownCnt; break;
                case -2 : ++jumpDownCnt; break;
                case -3 : ++tombDownCnt; break;
                case -4 : ++noChangeDownCnt; break;
                case 1 : ++normalUpCnt; break;
                case 2 : ++jumpUpCnt; break;
                case 3 : ++tombUpCnt; break;
                case 4 : ++noChangeUpCnt; break;
            }
        }
        totalUpCnt = normalUpCnt + jumpUpCnt + tombUpCnt + noChangeUpCnt;
        totalDownCnt = normalDownCnt + jumpDownCnt + tombDownCnt + noChangeDownCnt;
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
        for (int i = 1; i < len; i++) {
            if(changePct[i] > 0d && MathHelper.isEqual(end[i], max[i])){
                if(MathHelper.isEqual(changePct[i], 0.10, epsilon) ||
                        (MathHelper.isEqual(changePct[i], 0.05, epsilon) && isStLimit(i))){
                    if(MathHelper.isEqual(start[i], max[i])){
                        if(MathHelper.isEqual(min[i], max[i])){
                            result[i] = 4;
                        } else {
                            result[i] = 3;
                        }
                    } else if(max[i - 1] < min[i]){
                        result[i] = 2;
                    } else {
                        result[i] = 1;
                    }
                }
            } else if(changePct[i] < 0d && MathHelper.isEqual(end[i], min[i])){
                if(MathHelper.isEqual(changePct[i], -0.10, epsilon) ||
                        (MathHelper.isEqual(changePct[i], -0.05, epsilon) && isStLimit(i))){
                    if(MathHelper.isEqual(start[i], min[i])){
                        if(MathHelper.isEqual(min[i], max[i])){
                            result[i] = -4;
                        } else {
                            result[i] = -3;
                        }
                    }  else if(min[i - 1] > max[i]){
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
        for (int j = Math.max(i - 1, 0); j >= Math.max(i - 10, 1); j--) {
            if(Math.abs(MathStockUtil.calculateChangePct(end[j - 1], max[j])) - 0.05 > epsilon
                    || Math.abs(MathStockUtil.calculateChangePct(end[j - 1], min[j])) - 0.05 > epsilon) return false;
        }
        return true;
    }
}
