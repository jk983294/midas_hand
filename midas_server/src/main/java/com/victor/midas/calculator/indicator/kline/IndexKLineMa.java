package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.IndexFactory;
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

    static {
        IndexFactory.addCalculator(INDEX_NAME, new IndexKLineMa(IndexFactory.parameter));
    }

    @Override
    public void setRequiredCalculator() {
        requiredCalculator.add(IndexChangePct.INDEX_NAME);
    }

    private final static int TIME_FRAME = 5;

    private double[] middleShadowPctAvg;
    private double[] middleShadowPctStd;

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;

    private int len;

    public IndexKLineMa(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
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
    protected void calculateFromExisting() throws MidasException {
        calculateFromScratch();
    }

    @Override
    protected void calculateForTrain() throws MidasException {
        for (int i = 1; i < len; i++) {
            upShadowPct[i] = MathStockUtil.calculatePct(end[i - 1], max[i] - Math.max(start[i], end[i]));
            downShadowPct[i] = MathStockUtil.calculatePct(end[i - 1], Math.min(start[i], end[i]) - min[i]);
            middleShadowPct[i] = MathStockUtil.calculatePct(end[i - 1], end[i] - start[i]);
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        len = end.length;

        middleShadowPctAvg = new double[len];
        middleShadowPctStd = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
//        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
//        len = end.weight;
//        changePct = (double[])stock.queryCmpIndex(INDEX_NAME);
    }

    @Override
    public void applyParameter() {

    }
}
