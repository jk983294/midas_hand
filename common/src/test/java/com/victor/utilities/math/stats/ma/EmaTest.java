package com.victor.utilities.math.stats.ma;

import com.victor.utilities.visual.VisualAssist;

/**
 * test simple moving average
 */
public class EmaTest {

    public static void main(String[] args) {
        EMA ema = new EMA();
        double[] data = {1,2,3,4,5,6,7,8,9};
        double[] result = ema.calculate(data, 3);
        VisualAssist.print(result);


        double[] newData = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};
        double[] newResult = ema.calculate(newData, result, 3);
        VisualAssist.print(newResult);
    }
}
