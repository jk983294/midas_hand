package com.victor.midas.calculator.macd;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.divergence.IndexBadDepth;
import com.victor.midas.calculator.indicator.IndexChangePct;
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
public class IndexMacdSimpleSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "macd";

    private MaBase maMethod = new EMA();

    private double[] dif, dea, macdBar; // white line, yellow line, bar

    public IndexMacdSimpleSignal(CalcParameter parameter, MaBase maMethod) {
        super(parameter);
        this.maMethod = maMethod;
    }

    public IndexMacdSimpleSignal(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexBadDepth.INDEX_NAME);
        requiredCalculators.add(IndexMACD.INDEX_NAME);
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

    }

    @Override
    protected void initIndex() throws MidasException {
    }
}
