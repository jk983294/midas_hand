package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate K line basic, find past several days, it is small range oscillate
 */
public class IndexKLineSignals extends IndexCalcBase {

    public final static String INDEX_NAME = "k_sig";

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

    private int count;              // total day count satisfy some condition
    private int upShadowCount;      // for those days satisfy some condition whose upShadow > downShadow days
    private int downShadowCount;    // for those days satisfy some condition whose upShadow < downShadow days
    private int upCount;            // for those days satisfy some condition whose middlePct > 0 days
    private int downCount;          // for those days satisfy some condition whose middlePct < 0 days

    public IndexKLineSignals(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        for (itr = 1; itr < len; itr++) {
            signals[itr] = backInduction(itr);
        }

        addIndexData(INDEX_NAME, signals);
    }


    /**
     * backward induction n days before index satisfy some condition
     */
    private int backInduction(int index){
        upCount = downCount = downShadowCount = upShadowCount = count = 0;
        for (int i = index; i >= 0; --i) {
            if(Math.abs(changePct[i]) > 0.03){ break; }
            else if(middleShadowPct[i] > -0.01 && middleShadowPct[i] < 0.02){
                ++count;
                if(upShadowPct[i] > downShadowPct[i]){ ++upShadowCount; }
                else { ++downShadowCount; }
                if(middleShadowPct[i] > 0){ ++upCount; }
                else { ++downCount; }
            } else { break; }
        }
        /**
         * take care, make sure more downShadow and more up trend
         */
        if(upShadowCount >= downShadowCount || downCount >= upCount){
            count = 0;
        }
        return count;
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
