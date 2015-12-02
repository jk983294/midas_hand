package com.victor.midas.calculator.indicator;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.ArrayHelper;

import java.util.HashMap;

/**
 * calculate change percentage per day
 */
public class IndexChangePct extends IndexCalcBase {

    public final static String INDEX_NAME = MidasConstants.INDEX_NAME_CHANGEPCT;

    static {
        IndexFactory.addCalculator(INDEX_NAME, new IndexChangePct(IndexFactory.parameter));
    }

    private double[] changePct;

    private double[] end;

    private int len;

    public IndexChangePct(CalcParameter parameter) {
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
            changePct[i] = MathStockUtil.calculateChangePct(end[i - 1], end[i]);
        }
        addIndexData(INDEX_NAME, changePct);
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        double[] oldChangePct = (double[])oldStock.queryCmpIndex(getIndexName());

        changePct = ArrayHelper.copyToNewLenArray(oldChangePct, len);
        for (int i = Math.max(1, oldChangePct.length); i < len; i++) {
            changePct[i] = MathStockUtil.calculateChangePct(end[i - 1], end[i]);
        }
        addIndexData(INDEX_NAME, changePct);
    }

    @Override
    protected void calculateForTrain() throws MidasException {
        for (int i = 1; i < len; i++) {
            changePct[i] = MathStockUtil.calculateChangePct(end[i - 1], end[i]);
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        changePct = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        changePct = (double[])stock.queryCmpIndex(INDEX_NAME);
    }

    @Override
    public void applyParameter() {

    }


}
