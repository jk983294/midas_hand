package com.voctor.midas.calculator.util;


import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

public class MaxMinUtilTest {

    @Test
    public void test() throws MidasException {
        int cnt = 20;
        double start[] = new double[]{1.1, 1.2, 1.3, 1.4, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 1.2, 1.1, 1.3, 1.4, 1.6, 1.4, 1.3, 1.2, 1.1, 1.4};
        double end[] = new double[]  {1.2, 1.3, 1.4, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 1.2, 1.1, 1.3, 1.4, 1.6, 1.4, 1.3, 1.2, 1.1, 1.4, 1.5};

        double min[] = new double[]{1.1, 1.2, 1.3, 1.4, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 1.2, 1.1, 1.3, 1.4, 1.6, 1.4, 1.3, 1.2, 1.1, 1.4};
        double max[] = new double[]{1.2, 1.3, 1.4, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 1.2, 1.1, 1.3, 1.4, 1.6, 1.4, 1.3, 1.2, 1.1, 1.4, 1.5};
        StockVo stock = new StockVo();
        stock.addIndex(MidasConstants.INDEX_NAME_START, start);
        stock.addIndex(MidasConstants.INDEX_NAME_END, end);
        stock.addIndex(MidasConstants.INDEX_NAME_MAX, max);
        stock.addIndex(MidasConstants.INDEX_NAME_MIN, min);
        MaxMinUtil maxMinUtil = new MaxMinUtil();
        maxMinUtil.setUseEndStartPair(false);
        maxMinUtil.init(stock);
        maxMinUtil.calcMaxMinIndex(5);
        VisualAssist.print("max", maxMinUtil.getMaxIndex());
        VisualAssist.print("min", maxMinUtil.getMinIndex());

        VisualAssist.print("max", maxMinUtil.getMaxIndexRecursive(6));
        VisualAssist.print("min", maxMinUtil.getMinIndexRecursive(15));
    }
}
