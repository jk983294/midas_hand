package com.victor.midas.calculator.common;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;
import java.util.Map;

/**
 * demo calculator
 */
public class IndexDemo extends IndexCalcbase {

    private final static String indexName = "demoIdx";

    private double[] demo;

    private double[] end;

    private int len;

    public IndexDemo(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        for (int i = 1; i < len; i++) {
            demo[i] = MathStockUtil.calculateChangePct(end[i - 1], end[i]);
        }
        addIndexData(indexName, demo);
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
        demo = (double[])stock.queryCmpIndex(indexName);
    }

    @Override
    public void applyParameter() {}
}
