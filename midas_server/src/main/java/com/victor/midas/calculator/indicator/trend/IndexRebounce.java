package com.victor.midas.calculator.indicator.trend;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate Rebounce, find past several days big fall, fall energy is exhausted
 */
public class IndexRebounce extends IndexCalcbase {

    private final static String indexName = "rbs";

    private int[] signals;

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;
    private double[] total;
    private double[] changePct;
    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaYearD1;
    private double[] pMaLongD1;
    private double[] vMa;

    private int len;
    private int count;
    private int upCount;
    private int downCount;
    private double fallPct;

    public IndexRebounce(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        for (int i = 1; i < len; i++) {
            signals[i] = backInduction(i);
        }

        addIndexData(indexName, signals);
    }


    /**
     * backward induction n days before index satisfy some condition
     */
    private int backInduction(int index){
        upCount = downCount = count = 0;
        fallPct = 0;
        for (int i = index; i >= 0; --i) {
            if(changePct[i] > 0) break;
            else {
                fallPct += changePct[i];
                ++downCount;
            }
        }
        /**
         * take care, make sure more downShadow and more up trend
         */
        if(fallPct < -0.1 && downCount >= 4){
            if(total[index - 1] / total[index - 2] > 1.3 && total[index - 1] / total[index] > 1.3) return 1;
        }
        return 0;
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        calculateFromScratch();
    }

    @Override
    protected void calculateForTrain() throws MidasException {
//        for (int i = 1; i < len; i++) {
//            upShadowPct[i] = MathStockUtil.calculatePct(end[i - 1], max[i] - Math.max(start[i], end[i]));
//            downShadowPct[i] = MathStockUtil.calculatePct(end[i - 1], Math.min(start[i], end[i]) - min[i]);
//            middleShadowPct[i] = MathStockUtil.calculatePct(end[i - 1], Math.abs(start[i] - end[i]));
//        }
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
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        pMaYearD1 = (double[])stock.queryCmpIndex("pMa60D1");
        pMaLongD1 = (double[])stock.queryCmpIndex("pMa20D1");
        vMa = (double[])stock.queryCmpIndex("vMaMedium");
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        len = end.length;

        signals = new int[len];
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
//        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
//        len = end.length;
//        changePct = (double[])stock.queryCmpIndex(indexName);
    }

    @Override
    public void applyParameter() {

    }
}
