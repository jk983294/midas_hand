package com.voctor.midas.generic;

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
