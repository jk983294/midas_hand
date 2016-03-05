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

    public static final String INDEX_NAME = "pMA";

    private MaBase maMethod = new SMA();

    private double[] pMa5;
    private double[] pMa10;
    private double[] pMa20;
    private double[] pMa30;
    private double[] pMa60;
    private double[] pMaDiff;
    private double[] pMaDiffPct;

    public IndexPriceMA(CalcParameter parameter, MaBase maMethod) {
        super(parameter);
        this.maMethod = maMethod;
    }

    public IndexPriceMA(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    public String getIndexCmpName(int interval) {
        return INDEX_NAME + interval;
    }

    @Override
    public void calculate() throws MidasException {
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
    protected void initIndex() throws MidasException {
    }
}
