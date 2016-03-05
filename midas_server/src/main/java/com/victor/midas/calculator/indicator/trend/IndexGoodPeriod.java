package com.victor.midas.calculator.indicator.trend;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.kline.IndexKState;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.strategy.CalcUtilGp;
import com.victor.midas.model.common.KState;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.HashMap;

/**
 * good period is to pick some good period to select drop time intervene
 */
public class IndexGoodPeriod extends IndexCalcBase {

    public final static String INDEX_NAME = "gp";

    private final static int NOTHING = 0;
    private final static int PREPARE = 1;
    private final static int WILLBUY = 2;
    private final static int SELL = -1;
    private final static int SELL_KSTATE = -2;

    private CalcUtilGp calcUtil;

    private int[] k_state;
    private int[] gp_sig;
    private double[] gpShort;
    private double[] gpLong;

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] middleShadowPctAvg;
    private double[] middleShadowPctStd;
    //private int[] volPriceCorr;

    private int totalCnt;
    private int smallEntityCnt, bigEntityCnt;
    private int upEntityCnt, downEntityCnt;
    private int lgtValue;
    private double avgUpVolume, avgDownVolume;

    public IndexGoodPeriod(CalcParameter parameter) {
        super(parameter);
        calcUtil = new CalcUtilGp();
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexKState.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        gpShort = calcGp(10);
        gpLong = calcGp(30);
        calcSignals();

        addIndexData("gp10", gpShort);
        addIndexData("gp30", gpLong);
        addIndexData("gp_sig", gp_sig);
    }

    private void calcSignals(){
        int upSerialCnt = 0, downSerialCnt = 0;
        double upPctAccum = 0, downPctAccum = 0, prevUpPctAccum = 0, prevDownPctAccum = 0;
        for (int i = 30; i < len; i++) {
            if(middleShadowPct[i] > 0){
                ++upSerialCnt;
                upPctAccum += (upSerialCnt == 1 ? middleShadowPct[i] : changePct[i]);
                prevDownPctAccum = downPctAccum;
                downSerialCnt = 0;
                downPctAccum = 0;
            } else {
                ++downSerialCnt;
                downPctAccum += (downSerialCnt == 1 ? middleShadowPct[i] : changePct[i]);
                prevUpPctAccum = upPctAccum;
                upSerialCnt = 0;
                upPctAccum = 0;
            }
            if(gpLong[i] > 0.55 && gpShort[i] > 0.55 && calcUtil.isTrendUp(i)
//                    && !VolPriceCorrelation.isBadHugeVolume(volPriceCorr[i])
                    ){
                gp_sig[i] = PREPARE;
                if(((Math.abs(downPctAccum) > middleShadowPctAvg[i] * 2)
                        || (downSerialCnt >= 2 && Math.abs(changePct[i]) < middleShadowPctAvg[i]))
                        && calcUtil.isTrendOK(i)) gp_sig[i] = WILLBUY;
                if(upSerialCnt > 4 || upPctAccum > (middleShadowPctAvg[i - 1] * 2 + middleShadowPctStd[i - 1])) gp_sig[i] = SELL;
            }
            if(KState.isSellDecision(k_state[i])) gp_sig[i] = SELL_KSTATE;

        }
    }

    private double[] calcGp(int timeFrame){
        double[] gp = new double[len];
        smallEntityCnt = bigEntityCnt = upEntityCnt = downEntityCnt = totalCnt = lgtValue = 0;
        for (int i = 0; i < Math.min(timeFrame, len); i++) {
            statisticAdd(i);
            gp[i] = calcCurrentGp();
        }
        for (int i = timeFrame; i < len; i++) {
            statisticAdd(i);
            statisticRemove(i - timeFrame);
            gp[i] = calcCurrentGp();
        }
        return gp;
    }

    private double calcCurrentGp(){
        return (double)upEntityCnt / totalCnt;
    }

    private void statisticAdd(int index){
        ++totalCnt;
        upEntityCnt += (middleShadowPct[index] > 0.0 ? 1 : 0);
        downEntityCnt += (middleShadowPct[index] > 0.0 ? 0 : 1);
        smallEntityCnt += (calcUtil.isSmallEntity(index) ? 1 : 0);
        bigEntityCnt += (calcUtil.isBigEntity(index) ? 0 : 1);
    }

    private void statisticRemove(int index){
        --totalCnt;
        upEntityCnt -= (middleShadowPct[index] > 0.0 ? 1 : 0);
        downEntityCnt -= (middleShadowPct[index] > 0.0 ? 0 : 1);
        smallEntityCnt -= (calcUtil.isSmallEntity(index) ? 1 : 0);
        bigEntityCnt -= (calcUtil.isBigEntity(index) ? 0 : 1);
    }

    @Override
    protected void initIndex() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        middleShadowPctAvg = (double[])stock.queryCmpIndex("k_m_avg");
        middleShadowPctStd = (double[])stock.queryCmpIndex("k_m_std");
        //volPriceCorr = (int[])stock.queryCmpIndex("vp_corr");
        k_state = (int[])stock.queryCmpIndex("k_state");

        gp_sig = new int[len];
        gpShort = new double[len];
        gpLong = new double[len];
        calcUtil.init(stock);
    }
}
