package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * calculate K line basic, compare with yesterday's end price
 */
public class IndexKLine extends IndexCalcBase {

    public final static String INDEX_NAME = "k";

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;

    public IndexKLine(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        for (itr = 1; itr < len; itr++) {
            upShadowPct[itr] = MathStockUtil.calculatePct(end[itr - 1], max[itr] - Math.max(start[itr], end[itr]));
            downShadowPct[itr] = MathStockUtil.calculatePct(end[itr - 1], Math.min(start[itr], end[itr]) - min[itr]);
            middleShadowPct[itr] = MathStockUtil.calculatePct(end[itr - 1], end[itr] - start[itr]);
        }

        addIndexData("k_u", upShadowPct);
        addIndexData("k_d", downShadowPct);
        addIndexData("k_m", middleShadowPct);
    }

    @Override
    protected void initIndex() throws MidasException {
        upShadowPct = new double[len];
        downShadowPct = new double[len];
        middleShadowPct = new double[len];
    }
}
