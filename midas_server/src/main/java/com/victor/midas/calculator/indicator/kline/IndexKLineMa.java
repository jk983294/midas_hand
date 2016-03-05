package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;

/**
 * calculate K line basic, compare with yesterday's end price
 */
public class IndexKLineMa extends IndexCalcBase {

    private final static String INDEX_NAME = "k_MA";

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    private final static int TIME_FRAME = 5;

    private double[] middleShadowPctAvg;
    private double[] middleShadowPctStd;

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;

    public IndexKLineMa(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        DescriptiveStatistics descriptivestats = new DescriptiveStatistics();
        descriptivestats.setWindowSize(TIME_FRAME);
        for( int i = 0; i < len; i++) {
            descriptivestats.addValue(Math.abs(middleShadowPct[i]));
            middleShadowPctAvg[i] = descriptivestats.getMean();
            middleShadowPctStd[i] = descriptivestats.getStandardDeviation();
        }

        addIndexData("k_m_avg", middleShadowPctAvg);
        addIndexData("k_m_std", middleShadowPctStd);
    }

    @Override
    protected void initIndex() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");

        middleShadowPctAvg = new double[len];
        middleShadowPctStd = new double[len];
    }
}
