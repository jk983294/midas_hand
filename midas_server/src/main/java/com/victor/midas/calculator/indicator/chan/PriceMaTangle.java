package com.victor.midas.calculator.indicator.chan;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.calculator.indicator.IndexPriceMA;
import com.victor.midas.calculator.indicator.IndexVolumeMa;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.commons.math3.util.MathArrays;

import java.util.HashMap;
import java.util.Map;

/**
 * Price Moving Average
 * http://blog.sina.com.cn/s/blog_486e105c010007dc.html
 */
public class PriceMaTangle extends IndexCalcbase {

    public static final String INDEX_NAME = "pMaTangle";

    public static final int BUY = 1;
    public static final int SELL = -1;
    public static final int Watch = 0;

    /** threshold for determine tangle state*/
    private double bullLine;
    private double bearLine;

    /** from stockVo*/
    private double[] pMaDiffPct;
    private int[] bullPeriod;
    private int[] bearPeriod;
    private int[] tanglePeriod;
    private int[] decision;

    private int len;

    public PriceMaTangle(CalcParameter parameter) {
        super(parameter);
    }
    public PriceMaTangle() {}

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        // calculate tangle range
        bullPeriod = MathStockUtil.thresholdCalc(pMaDiffPct, bullLine);
        bearPeriod = MathStockUtil.thresholdVectorRevert(MathStockUtil.thresholdCalc(pMaDiffPct, bearLine));
        tanglePeriod = MathStockUtil.thresholdCalc(pMaDiffPct, bullLine , bearLine);

        calculateDecision();

        addIndexData("bullPeriod", bullPeriod);
        addIndexData("bearPeriod", bearPeriod);
        addIndexData("tanglePeriod", tanglePeriod);
        addIndexData("decision", decision);
    }

    private void calculateDecision(){
        for (int i = 5; i < len; i++) {
//            if(tanglePeriod[i] == MathStockUtil.THRESHOLD_IN_RANGE
//                    && bearPeriod[i - 2] == MathStockUtil.THRESHOLD_ABOVE){
//                decision[i] = BUY;
//            } else if(tanglePeriod[i] == MathStockUtil.THRESHOLD_IN_RANGE
//                    && bullPeriod[i - 2] == MathStockUtil.THRESHOLD_ABOVE){
//                decision[i] = SELL;
//            } else {
//                decision[i] = Watch;
//            }
            /** turn point confirm for pMaDiff at point i */
            if(pMaDiffPct[i] > bearLine &&  pMaDiffPct[i] < pMaDiffPct[i-1] && pMaDiffPct[i- 1] > pMaDiffPct[i-2]){
                decision[i] = SELL;
            } else if(pMaDiffPct[i] < bullLine && pMaDiffPct[i] > pMaDiffPct[i-1] && pMaDiffPct[i- 1] < pMaDiffPct[i-2]){
                decision[i] = BUY;
            } else {
                decision[i] = Watch;
            }
        }
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        calculateFromScratch();
    }

    @Override
    protected void calculateForTrain() throws MidasException {
//        MathStockUtil.thresholdCalcInplace(pMaDiffPct, bullLine, bullPeriod);
//        MathStockUtil.thresholdVectorRevertInplace(MathStockUtil.thresholdCalc(pMaDiffPct, bearLine), bearPeriod);
//        MathStockUtil.thresholdCalcInplace(pMaDiffPct, bullLine , bearLine, tanglePeriod);
        calculateDecision();
    }

    @Override
    protected void initIndex() throws MidasException {
        pMaDiffPct = (double[])stock.queryCmpIndex("pMaDiffPct");
        len = pMaDiffPct.length;
        bullPeriod = new int[len];
        bearPeriod = new int[len];
        tanglePeriod = new int[len];
        decision = new int[len];
        cmpIndexName2Index = new HashMap<>();
    }

    @Override
    protected void initIndexForTrain() throws MidasException {
        pMaDiffPct = (double[])stock.queryCmpIndex("pMaDiffPct");
        len = pMaDiffPct.length;
        bullPeriod = (int[])stock.queryCmpIndex("bullPeriod");
        bearPeriod = (int[])stock.queryCmpIndex("bearPeriod");
        tanglePeriod = (int[])stock.queryCmpIndex("tanglePeriod");
        decision = (int[])stock.queryCmpIndex("decision");
    }

    @Override
    public void applyParameter() {
        bullLine = parameter.getBullLine();
        bearLine = parameter.getBearLine();
    }
}
