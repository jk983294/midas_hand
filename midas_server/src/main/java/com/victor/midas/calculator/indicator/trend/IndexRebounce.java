package com.victor.midas.calculator.indicator.trend;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.indicator.kline.IndexKState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate Rebounce, find past several days big fall, fall energy is exhausted
 */
public class IndexRebounce extends IndexCalcBase {

    private final static String INDEX_NAME = "rbs";

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
    }

    private int[] signals;

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaYearD1;
    private double[] pMaLongD1;
    private double[] vMa;

    private int count;
    private int upCount;
    private int downCount;
    private double fallPct;

    public IndexRebounce(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        for (int i = 1; i < len; i++) {
            signals[i] = backInduction(i);
        }

        addIndexData(INDEX_NAME, signals);
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
    protected void initIndex() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        pMaYearD1 = (double[])stock.queryCmpIndex("pMa60D1");
        pMaLongD1 = (double[])stock.queryCmpIndex("pMa20D1");
        vMa = (double[])stock.queryCmpIndex("vMaMedium");

        signals = new int[len];
    }
}
