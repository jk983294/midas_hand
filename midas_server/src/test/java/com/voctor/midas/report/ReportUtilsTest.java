package com.voctor.midas.report;

import com.victor.midas.report.BalanceSheetExtractor;
import com.victor.midas.report.ReportUtils;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * generic unit test
 */
public class ReportUtilsTest {

    private static final Logger logger = Logger.getLogger(ReportUtilsTest.class);

    @Test
    public void test() {
        assertEquals(RegExpHelper.isMatch("资 产 负 债 表", BalanceSheetExtractor.balanceSheetHeaderPattern), true);
        assertEquals(RegExpHelper.isMatch("资产负债表", BalanceSheetExtractor.balanceSheetHeaderPattern), true);
        assertEquals(ReportUtils.isDate("2015年 12月 3日"), true);
        assertEquals(ReportUtils.isDate("2015年 12月 13日"), true);
        assertEquals(ReportUtils.isDate("2015年12月113日"), false);
    }
}
