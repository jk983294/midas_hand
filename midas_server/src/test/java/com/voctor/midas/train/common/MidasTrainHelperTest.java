package com.voctor.midas.train.common;


import com.victor.midas.train.common.MidasTrainHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MidasTrainHelperTest {

    @Test
    public void test1() throws InterruptedException {
        double[] data = new double[]{0, 5, 0, -5, 0, 0, 0, 4, 0, -5};
        assertEquals(MidasTrainHelper.getHoldingTime(data, 1, 0, 1), 3);
        assertEquals(MidasTrainHelper.getHoldingTime(data, 7, 0, 1), 3);
    }

    @Test
    public void test2() throws InterruptedException {
        double[] data = new double[]{0, 5, 0, -5, 0, 0, 0, 4, 0, -5};
        assertEquals(MidasTrainHelper.getHoldingTime(data, 1, 0, 0), 4);
        assertEquals(MidasTrainHelper.getHoldingTime(data, 7, 0, 0), 3);
    }

    @Test
    public void test3() throws InterruptedException {
        double[] data = new double[]{0, 5, 0, -5, 0, 0, 0, 4, 0, 0};
        assertEquals(MidasTrainHelper.getHoldingTime(data, 1, 0, 0), 4);
        assertEquals(MidasTrainHelper.getHoldingTime(data, 7, 0, 0), 3);
    }
}
