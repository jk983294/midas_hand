package com.victor.midas.calculator.indicator.trend;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.MathDeltaUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate price delta, derivative of price trend, use priceMaYear as pd0
 */
public class IndexPriceDelta extends IndexCalcBase {

    public static final String INDEX_NAME = "pD";

    @Override
    public void setRequiredCalculators() {
    }

    private int timeFramePriceDelta = 6;


    private double[] pMa5;
    private double[] pMa5D1;
    private double[] pMa10;
    private double[] pMa10D1;
    private double[] pMa20;
    private double[] pMa20D1;
    private double[] pMa30;
    private double[] pMa30D1;
    //private double[] pMaYearD2;
    private double[] pMa60;
    private double[] pMa60D1;
    private double[] pD1;           // using end

    private MathDeltaUtil deltaUtil;

    public IndexPriceDelta(CalcParameter parameter) {
        super(parameter);
        deltaUtil = new MathDeltaUtil(timeFramePriceDelta);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        pMa30D1 = deltaUtil.calculate(pMa30, false);
        //pMaYearD2 = deltaUtil.calculate(pMa30D1, false);

        pMa60D1 = deltaUtil.calculate(pMa60, false);

        pMa10D1 = deltaUtil.calculate(pMa10, false);

        pMa5D1 = deltaUtil.calculate(pMa5, false);

        pMa20D1 = deltaUtil.calculate(pMa20, false);

        pD1 = deltaUtil.calculate(end, false);

        addIndexData("pMa30D1", pMa30D1);
        //addIndexData("pMaYearD2", pMaYearD2);
        addIndexData("pMa60D1", pMa60D1);
        addIndexData("pMa10D1", pMa10D1);
        addIndexData("pMa5D1", pMa5D1);
        addIndexData("pMa20D1", pMa20D1);
        addIndexData("pD1", pMa5D1);
    }

    @Override
    protected void initIndex() throws MidasException {
        pMa5 = (double[])stock.queryCmpIndex("pMa5");
        pMa10 = (double[])stock.queryCmpIndex("pMa10");
        pMa20 = (double[])stock.queryCmpIndex("pMa20");
        pMa30 = (double[])stock.queryCmpIndex("pMa30");
        pMa60 = (double[])stock.queryCmpIndex("pMa60");
    }

}
