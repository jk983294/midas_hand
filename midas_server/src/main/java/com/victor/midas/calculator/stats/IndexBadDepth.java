package com.victor.midas.calculator.stats;


import com.victor.midas.calculator.common.MarketIndexAggregationCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;

/**
 * this is used for get big picture when should let go cause it is too risky
 */
public class IndexBadDepth extends MarketIndexAggregationCalcBase {

    public final static String INDEX_NAME = "badDepth";

    public IndexBadDepth(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        double[] marketIndexEnd = (double[])marketIndex.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        double[] marketIndexTotal = (double[])marketIndex.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        double changePctFromMa, volumeChange;
        // check market index, price position against price MA
        MaBase maMethod = new SMA();
        double[] middleShadowPct = (double[])marketIndex.queryCmpIndex("k_m");
        MaxMinUtil mmPriceUtil60 = new MaxMinUtil(marketIndex, false);
        mmPriceUtil60.calcMaxMinIndex(60);
        MaxMinUtil mmPriceUtil5 = new MaxMinUtil(marketIndex, false);
        mmPriceUtil5.calcMaxMinIndex( 15);

        double[] badDepth = new double[len];

        double[] ma = maMethod.calculate(marketIndexEnd, 5);
        double[] vMa = maMethod.calculate(marketIndexTotal, 5);
        for( int i = 5; i < len; i++) {
            changePctFromMa = MathStockUtil.calculateChangePct(marketIndexEnd[i], ma[i]);
            volumeChange = MathStockUtil.calculateChangePct(vMa[i - 5], marketIndexTotal[i]);
            if(marketIndexEnd[i] < ma[i] && changePctFromMa < 0.0091 && Math.abs(middleShadowPct[i]) < 0.0051){
                badDepth[i] = -5d;
            }
        }

        marketIndex.addIndex("badDepth", badDepth);
    }


}
