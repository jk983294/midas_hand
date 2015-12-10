package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate K line basic, find past several days, it is small range oscillate
 */
public class IndexKLineSignals extends IndexCalcBase {

    public final static String INDEX_NAME = "k_sig";

    static {
        IndexFactory.addCalculator(INDEX_NAME, new IndexLongGoodTrend(IndexFactory.parameter));
    }

    @Override
    public void setRequiredCalculator() {
        requiredCalculator.add(IndexChangePct.INDEX_NAME);
    }

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
    protected void calculateFromScratch() throws MidasException {
        for (int i = 1; i < len; i++) {
            signals[i] = backInduction(i);
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
//        len = end.weight;
//        changePct = (double[])stock.queryCmpIndex(INDEX_NAME);
    }

    @Override
    public void applyParameter() {

    }
}
