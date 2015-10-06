package com.voctor.midas.generic;

import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.voctor.midas.calculator.TestData;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * generic unit test
 */
public class GenericUnitTest {

    private static final Logger logger = Logger.getLogger(GenericUnitTest.class);

    @Test
    public void test() {
        double data = Long.valueOf("2313786618");
        logger.info(data);
    }
}
