package com.victor.midas.calculator.indicator;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.util.MathArrays;

import java.util.HashMap;

/**
 * calculate Price Moving Average
 */
public class IndexPriceMA extends IndexCalcBase {

    private static final String INDEX_NAME = "pMA";

    static {
        IndexFactory.addCalculator(INDEX_NAME, new IndexPriceMA(IndexFactory.parameter, new SMA()));
    }

    private MaBase maMethod;

    private double[] end;

    private double[] pMa5;
    private double[] pMa10;
    private double[] pMa20;
    private double[] pMa30;
    private double[] pMa60;
    private double[] pMaDiff;
    private double[] pMaDiffPct;

    private int len;

    public IndexPriceMA(CalcParameter parameter, MaBase maMethod) {
        super(parameter);
        this.maMethod = maMethod;
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculator() {

    }

    public String getIndexCmpName(int interval) {
        return INDEX_NAME + interval;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        pMa5 = maMethod.calculate(end, 5);
        pMa10 = maMethod.calculate(end, 10);
        pMa20 = maMethod.calculate(end, 20);
        pMa30 = maMethod.calculate(end, 30);
        pMa60 = maMethod.calculate(end, 60);
        // calculate short and long term ma difference
        pMaDiff = MathArrays.ebeSubtract(pMa5, pMa10);
        // calculate short and long term ma difference percentage
        pMaDiffPct = MathStockUtil.differencePct(pMa5, pMa10);

        addIndexData("pMa5", pMa5);
        addIndexData("pMa10", pMa10);
        addIndexData("pMa20", pMa20);
        addIndexData("pMa30", pMa30);
        addIndexData("pMa60", pMa60);
        //addIndexData("pMaDiff", pMaDiff);
        //addIndexData("pMaDiffPct", pMaDiffPct);
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        String indexCmpName;
        double[] oldIndexValue;
        indexCmpName = getIndexCmpName(5);
        oldIndexValue = (double[])oldStock.queryCmpIndex(indexCmpName);
        pMa5 = maMethod.calculate(end, oldIndexValue, 5);
        indexCmpName = getIndexCmpName(10);
        oldIndexValue = (double[])oldStock.queryCmpIndex(indexCmpName);
        pMa10 = maMethod.calculate(end, oldIndexValue, 10);
        indexCmpName = getIndexCmpName(20);
        oldIndexValue = (double[])oldStock.queryCmpIndex(indexCmpName);
        pMa20 = maMethod.calculate(end, oldIndexValue, 20);
        indexCmpName = getIndexCmpName(30);
        oldIndexValue = (double[])oldStock.queryCmpIndex(indexCmpName);
        pMa30 = maMethod.calculate(end, oldIndexValue, 30);
        // calculate short and long term ma difference
        pMaDiff = MathArrays.ebeSubtract(pMa5, pMa10);
        // calculate short and long term ma difference percentage
        pMaDiffPct = MathStockUtil.differencePct(pMa5, pMa10);

        addIndexData("pMa5", pMa5);
        addIndexData("pMa10", pMa10);
        addIndexData("pMa20", pMa20);
        addIndexData("pMa30", pMa30);
        addIndexData("pMa60", pMa60);
        //addIndexData("pMaDiff", pMaDiff);
        //addIndexData("pMaDiffPct", pMaDiffPct);
    }

    @Override
    protected void calculateForTrain() throws MidasException {
        maMethod.calculateInPlace(end, 5, pMa5);
        maMethod.calculateInPlace(end, 10, pMa10);
        maMethod.calculateInPlace(end, 20, pMa20);
        maMethod.calculateInPlace(end, 30, pMa30);
        // calculate short and long term ma difference
        ArrayHelper.ebeSubtractInplace(pMa5, pMa10, pMaDiff);
        // calculate short and long term ma difference percentage
        MathStockUtil.differencePctInplace(pMa5, pMa10, pMaDiffPct);
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        pMa5 = (double[])stock.queryCmpIndex("pMa5");
        pMa10 = (double[])stock.queryCmpIndex("pMa10");
        pMa20 = (double[])stock.queryCmpIndex("pMa20");
        pMa30 = (double[])stock.queryCmpIndex("pMa30");
        pMaDiff = (double[])stock.queryCmpIndex("pMaDiff");
        pMaDiffPct = (double[])stock.queryCmpIndex("pMaDiffPct");
    }

    @Override
    public void applyParameter() {
    }
}
