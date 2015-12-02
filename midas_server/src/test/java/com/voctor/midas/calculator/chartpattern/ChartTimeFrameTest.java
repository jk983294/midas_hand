package com.voctor.midas.calculator.chartpattern;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.voctor.midas.calculator.TestData;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * ChartTimeFrame unit test
 */
public class ChartTimeFrameTest {

    private static final Logger logger = Logger.getLogger(ChartTimeFrameTest.class);

    @Test
    public void testChartTimeFrame() throws MidasException {
        IndexCalcBase calculator = IndexFactory.getIndexName2Calculator().get("ctf");
        IndexCalcBase indexChangePct = IndexFactory.getIndexName2Calculator().get(MidasConstants.INDEX_NAME_CHANGEPCT);
        StockVo stock = new TestData().getStockVo();
        indexChangePct.calculate(stock, null);
//        calculator.calculate(stock, null);
        logger.info(stock);
//        int[] data = (int[])stock.queryCmpIndex(calculator.getIndexName());
//        VisualAssist.print(data);
        logger.info(stock);
    }
}
