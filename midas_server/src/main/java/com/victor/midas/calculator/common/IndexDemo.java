package com.victor.midas.calculator.common;

import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * demo calculator
 */
public class IndexDemo extends IndexCalcBase {

    private final static String INDEX_NAME = "demoIdx";

    static {
        IndexFactory.addCalculator(INDEX_NAME, new IndexDemo(IndexFactory.parameter));
    }

    private double[] demo;

    private double[] end;

    private int len;

    public IndexDemo(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculator() {

    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        for (int i = 1; i < len; i++) {
            demo[i] = MathStockUtil.calculateChangePct(end[i - 1], end[i]);
        }
        addIndexData(INDEX_NAME, demo);
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        calculateFromScratch();
    }

    @Override
    protected void calculateForTrain() throws MidasException {
        for (int i = 1; i < len; i++) {
            demo[i] = MathStockUtil.calculateChangePct(end[i - 1], end[i]);
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        demo = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        demo = (double[])stock.queryCmpIndex(INDEX_NAME);
    }

    @Override
    public void applyParameter() {}
}
