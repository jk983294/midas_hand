package com.victor.midas.calculator.indicator.chan;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * Price Moving Average
 * http://blog.sina.com.cn/s/blog_486e105c010007dc.html
 */
public class PriceMaTangle extends IndexCalcBase {

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

    public PriceMaTangle(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
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
        for (itr = 5; itr < len; itr++) {
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
            if(pMaDiffPct[itr] > bearLine &&  pMaDiffPct[itr] < pMaDiffPct[itr-1] && pMaDiffPct[itr- 1] > pMaDiffPct[itr-2]){
                decision[itr] = SELL;
            } else if(pMaDiffPct[itr] < bullLine && pMaDiffPct[itr] > pMaDiffPct[itr-1] && pMaDiffPct[itr- 1] < pMaDiffPct[itr-2]){
                decision[itr] = BUY;
            } else {
                decision[itr] = Watch;
            }
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        pMaDiffPct = (double[])stock.queryCmpIndex("pMaDiffPct");

        bullPeriod = new int[len];
        bearPeriod = new int[len];
        tanglePeriod = new int[len];
        decision = new int[len];
    }

    @Override
    public void applyParameter(CalcParameter parameter) {
        this.parameter = parameter;
        bullLine = parameter.bullLine;
        bearLine = parameter.bearLine;
    }
}
