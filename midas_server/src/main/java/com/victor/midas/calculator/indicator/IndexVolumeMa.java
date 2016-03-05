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
public class IndexVolumeMa extends IndexCalcBase {

    private static final String INDEX_NAME = "vMA";

    private MaBase maMethod = new SMA();

    private int volumeMaShort;
    private int volumeMaMedium;
    private int volumeMaLong;
    private int volumeMaYear;

    private double[] vMaShort;
    private double[] vMaMedium;
    private double[] vMaLong;
    private double[] vMaYear;
    private double[] vMaDiff;
    private double[] vMaDiffPct;

    private double[] volRatio;

    public IndexVolumeMa(CalcParameter parameter, MaBase maMethod) {
        super(parameter);
        this.maMethod = maMethod;
    }

    public IndexVolumeMa(CalcParameter parameter) {
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
        vMaShort = maMethod.calculate(total, volumeMaShort);
        vMaMedium = maMethod.calculate(total, volumeMaMedium);
        vMaLong = maMethod.calculate(total, volumeMaLong);
        vMaYear = maMethod.calculate(total, volumeMaYear);
        // calculate short and long term ma difference
        vMaDiff = MathArrays.ebeSubtract(vMaShort, vMaMedium);
        // calculate short and long term ma difference percentage
        vMaDiffPct = MathStockUtil.differencePct(vMaShort, vMaMedium);

        volRatio = calcVolumeRatio();

        addIndexData("vMaShort", vMaShort);
        addIndexData("vMaMedium", vMaMedium);
        addIndexData("vMaLong", vMaLong);
        addIndexData("vMaYear", vMaYear);
//        addIndexData("vMaDiff", vMaDiff);
//        addIndexData("vMaDiffPct", vMaDiffPct);
        addIndexData("volRatio", volRatio);
    }

    private double[] calcVolumeRatio(){
        double[] results = new double[len];
        for (int i = 1; i < len; ++i){
            results[i] = total[i] / vMaMedium[i - 1];
        }
        return results;
    }

    @Override
    protected void initIndex() throws MidasException {
    }

    @Override
    public void applyParameter(CalcParameter parameter) {
        this.parameter = parameter;
        volumeMaShort = parameter.volumeMaShort;
        volumeMaMedium = parameter.volumeMaMedium;
        volumeMaLong = parameter.volumeMaLong;
        volumeMaYear = parameter.volumeMaYear;
    }
}
