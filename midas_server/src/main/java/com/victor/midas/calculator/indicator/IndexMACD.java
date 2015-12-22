package com.victor.midas.calculator.indicator;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.EMA;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.utils.MathHelper;

import java.util.HashMap;

/**
 * calculate Price Moving Average
 */
public class IndexMACD extends IndexCalcBase {

    public static final String INDEX_NAME = "macd";

    private MaBase maMethod = new EMA();

    private double[] end;

    private double[] pMa5;
    private double[] pMa35;
    private double[] macd;
    private double[] signalLine;
    private int len;

    public IndexMACD(CalcParameter parameter, MaBase maMethod) {
        super(parameter);
        this.maMethod = maMethod;
    }

    public IndexMACD(CalcParameter parameter) {
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
        pMa35 = maMethod.calculate(end, 35);
        macd = MathHelper.subtract(pMa5, pMa35);
        signalLine = maMethod.calculate(macd, 5);


        addIndexData("pMa5", pMa5);
        addIndexData("pMa35", pMa35);
        addIndexData("macd", macd);
        addIndexData("signalLine", signalLine);
        //addIndexData("pMaDiff", pMaDiff);
        //addIndexData("pMaDiffPct", pMaDiffPct);
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        len = end.length;
        cmpIndexName2Index = new HashMap<>();
    }
}
