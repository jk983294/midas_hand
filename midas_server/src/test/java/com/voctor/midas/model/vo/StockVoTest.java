package com.voctor.midas.model.vo;


import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.perf.PerfCollector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StockVoTest {

    @Test
    public void test1() throws InterruptedException {
        StockVo stock = new StockVo();
        int[] data = new int[]{20150601, 20150602, 20150603, 20150604, 20150605, 20150606, 20150607, 20150608, 20150609, 20150610};
        stock.setCobIndex(1);
        stock.setDatesInt(data);
        assertEquals(stock.getCobIndex(20150607), 6);
    }

    @Test
    public void test2() throws InterruptedException {
        StockVo stock = new StockVo();
        int[] data = new int[]{20150601, 20150602, 20150603, 20150604, 20150605, 20150606, 20150607, 20150608, 20150609, 20150610};
        stock.setCobIndex(7);
        stock.setDatesInt(data);
        assertEquals(stock.getCobIndex(20150607), -1);
    }

    @Test(expected = RuntimeException.class)
    public void test3() throws InterruptedException {
        StockVo stock = new StockVo();
        int[] data = new int[]{20150601, 20150602, 20150603, 20150604, 20150605, 20150606, 20150607, 20150608, 20150609, 20150610};
        stock.setCobIndex(7);
        stock.setDatesInt(data);
        assertEquals(stock.getCobIndex(20150611), -1);
    }
}
