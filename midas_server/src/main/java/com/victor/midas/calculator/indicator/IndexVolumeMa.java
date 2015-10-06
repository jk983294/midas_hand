package com.victor.midas.calculator.indicator;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.util.MathArrays;

import java.util.HashMap;
import java.util.Map;

/**
 * calculate Price Moving Average
 */
public class IndexVolumeMa extends IndexCalcbase {

    private static final String INDEX_NAME = "vMA";

    private MaBase maMethod;

    private int volumeMaShort;
    private int volumeMaMedium;
    private int volumeMaLong;
    private int volumeMaYear;

    private double[] total;

    private double[] vMaShort;
    private double[] vMaMedium;
    private double[] vMaLong;
    private double[] vMaYear;
    private double[] vMaDiff;
    private double[] vMaDiffPct;

    private double[] volRatio;

    private int len;

    public IndexVolumeMa(CalcParameter parameter, MaBase maMethod) {
        super(parameter);
        this.maMethod = maMethod;
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    public String getIndexCmpName(int interval) {
        return INDEX_NAME + interval;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
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

    @Override
    protected void calculateFromExisting() throws MidasException {
        double[] oldIndexValue;
        oldIndexValue = (double[])oldStock.queryCmpIndex("vMaShort");
        vMaShort = maMethod.calculate(total, oldIndexValue, volumeMaShort);
        oldIndexValue = (double[])oldStock.queryCmpIndex("vMaMedium");
        vMaMedium = maMethod.calculate(total, oldIndexValue, volumeMaMedium);
        oldIndexValue = (double[])oldStock.queryCmpIndex("vMaLong");
        vMaLong = maMethod.calculate(total, oldIndexValue, volumeMaLong);
        oldIndexValue = (double[])oldStock.queryCmpIndex("vMaYear");
        vMaYear = maMethod.calculate(total, oldIndexValue, volumeMaYear);
        // calculate short and long term ma difference
        vMaDiff = MathArrays.ebeSubtract(vMaShort, vMaMedium);
        // calculate short and long term ma difference percentage
        vMaDiffPct = MathStockUtil.differencePct(vMaShort, vMaMedium);

        volRatio = calcVolumeRatio();

        addIndexData("vMaShort", vMaShort);
        addIndexData("vMaMedium", vMaMedium);
        addIndexData("vMaLong", vMaLong);
        addIndexData("vMaYear", vMaYear);
        addIndexData("vMaDiff", vMaDiff);
        addIndexData("vMaDiffPct", vMaDiffPct);
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
    protected void calculateForTrain() throws MidasException {
        maMethod.calculateInPlace(total, volumeMaShort, vMaShort);
        maMethod.calculateInPlace(total, volumeMaMedium, vMaMedium);
        maMethod.calculateInPlace(total, volumeMaLong, vMaLong);
        maMethod.calculateInPlace(total, volumeMaYear, vMaYear);
        // calculate short and long term ma difference
        ArrayHelper.ebeSubtractInplace(vMaShort, vMaMedium, vMaDiff);
        // calculate short and long term ma difference percentage
        MathStockUtil.differencePctInplace(vMaShort, vMaMedium, vMaDiffPct);
    }

    @Override
    protected void initIndex() throws MidasException {
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        len = total.length;
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        len = total.length;
        vMaShort = (double[])stock.queryCmpIndex("vMaShort");
        vMaMedium = (double[])stock.queryCmpIndex("vMaMedium");
        vMaLong = (double[])stock.queryCmpIndex("vMaLong");
        vMaYear = (double[])stock.queryCmpIndex("vMaYear");
        vMaDiff = (double[])stock.queryCmpIndex("vMaDiff");
        vMaDiffPct = (double[])stock.queryCmpIndex("vMaDiffPct");
    }

    @Override
    public void applyParameter() {
        volumeMaShort = parameter.getVolumeMaShort();
        volumeMaMedium = parameter.getVolumeMaMedium();
        volumeMaLong = parameter.getVolumeMaLong();
        volumeMaYear = parameter.getVolumeMaYear();
    }
}
