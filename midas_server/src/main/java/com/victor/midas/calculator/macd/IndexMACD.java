package com.victor.midas.calculator.macd;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.EMA;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.utils.MathHelper;

import java.util.HashMap;

/**
 * calculate MACD
 */
public class IndexMACD extends IndexCalcBase {

    public static final String INDEX_NAME = "macd";

    private MaBase maMethod = new EMA();

    private double[] pMaFast;
    private double[] pMaSlow;
    private double[] dif, dea, macdBar; // white line, yellow line, bar

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

    @Override
    public void calculate() throws MidasException {
        pMaFast = maMethod.calculate(end, 12);
        pMaSlow = maMethod.calculate(end, 26);
        dif = MathHelper.subtract(pMaFast, pMaSlow);
        dea = maMethod.calculate(dif, 9);
        macdBar = MathHelper.multiplyInPlace(MathHelper.subtract(dif, dea), 2d);

        addIndexData("dif", dif);
        addIndexData("dea", dea);
        addIndexData("macdBar", macdBar);
    }

    @Override
    protected void initIndex() throws MidasException {
    }
}
