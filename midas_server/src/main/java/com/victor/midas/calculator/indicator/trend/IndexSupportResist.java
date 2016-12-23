package com.victor.midas.calculator.indicator.trend;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.calculator.util.MaxMinUtil;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.strategy.CalcUtilSR;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.KState;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.MathHelper;

import java.util.HashMap;

/**
 * good period is to pick some good period to select drop time intervene
 */
public class IndexSupportResist extends IndexCalcBase {

    private final static String INDEX_NAME = "sr";

    private final static int NOTHING = 0;
    private final static int PREPARE_NEW_HIGH = 1;
    private final static int PREPARE_STEADY_UP = 2;
    private final static int PREPARE_MIN_SUPPORT_REVERSE = 3;
    private final static int PREPARE_MA_SUPPORT_REVERSE = 4;
    private final static int WILLBUY = 5;
    private final static int SELL = -1;
    private final static int SELL_KSTATE = -2;

    public static boolean isPrepareBuy(int srSignalValue){
        return MathHelper.isInRange(srSignalValue, PREPARE_NEW_HIGH, PREPARE_MA_SUPPORT_REVERSE);
    }

    public static boolean isBuy(int srSignalValue){
        return srSignalValue == WILLBUY;
    }

    private CalcUtilSR calcUtil;

    private int[] k_state;
    private int[] sr_sig;

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaLong;               // 60 days price MA
    private int[] isBad;

    private int[] maxIndexLong;
    private int[] minIndexLong;
    private int[] maxIndexShort;
    private int[] minIndexShort;

    private double supportLine, newHighPrice, newLowPrice;
    private boolean hasTarget, hasCloseEnough;
    private int targetIndex = 0;
    private int prepareReason = 0;

    private static final int longTimeFrame = 60;
    private static final int shortTimeFrame = 10;

    private MaxMinUtil maxMinUtil;

    public IndexSupportResist(CalcParameter parameter) {
        super(parameter);
        calcUtil = new CalcUtilSR();
        maxMinUtil = new MaxMinUtil();
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        maxMinUtil.calcMaxMinIndex(longTimeFrame);
        maxIndexLong = maxMinUtil.getMaxIndex();
        minIndexLong = maxMinUtil.getMinIndex();
        maxMinUtil.calcMaxMinIndex(shortTimeFrame);
        maxIndexShort = maxMinUtil.getMaxIndex();
        minIndexShort = maxMinUtil.getMinIndex();
        calcSignals();
        addIndexData("sr_sig", sr_sig);
    }

    private void calcSignals(){
        int upSerialCnt = 0, downSerialCnt = 0;
        double upPctAccum = 0, downPctAccum = 0, prevUpPctAccum = 0, prevDownPctAccum = 0;
        supportLine = 0;
        newLowPrice = newHighPrice = 0;
        hasTarget = hasCloseEnough = false;
        targetIndex = 0;
        prepareReason = 0;
        for (itr = 30; itr < len; itr++) {
            // calculate trend percentage
            if(middleShadowPct[itr] > 0){
                ++upSerialCnt;
                upPctAccum += (upSerialCnt == 1 ? middleShadowPct[itr] : changePct[itr]);
                prevDownPctAccum = downPctAccum;
                downSerialCnt = 0;
                downPctAccum = 0;
            } else {
                ++downSerialCnt;
                downPctAccum += (downSerialCnt == 1 ? middleShadowPct[itr] : changePct[itr]);
                prevUpPctAccum = upPctAccum;
                upSerialCnt = 0;
                upPctAccum = 0;
            }
            /** check if PREPARE_STEADY_UP */
            if(isNewHighsNoCrazy(itr)){
                recordPrepareSupportMax(itr, PREPARE_STEADY_UP);
            }
//            /** check if new high, then prepare for retreat */
//            if(maxIndexLong[i] == i && maxIndexLong[i-1] != i-1 && i - maxIndexLong[i-1] > 5
//                    //&& calcUtil.isTrendUp(i)
//                    && !KState.isSellDecisionStrong(k_state[i])
//                    && changePct[i] > 0.01 && upPctAccum > 0.03
//                    ){
//                recordPrepareSupportMax(i, PREPARE_NEW_HIGH);
//            }
//            /** check if PREPARE_MIN_SUPPORT_REVERSE */
//            if(minIndexLong[i] != i && MathStockUtil.calculateChangePct(getMinPrice(minIndexLong[i]), end[i]) < 0.02){
//                recordPrepareSupportMin(i, PREPARE_MIN_SUPPORT_REVERSE);
//            }
            /** check if PREPARE_MA_SUPPORT_REVERSE */
//            if(minIndexShort[i] == i
//                    && MathHelper.isInRange(MathStockUtil.calculateChangePct(pMaLong[i], getMinPrice(i)), 0.0, 0.05)){
//                recordPrepareSupportMa(i, PREPARE_MA_SUPPORT_REVERSE);
//            }
            /** if in prepare state, then wait retreat */
            if(hasTarget){
                if(isBad[itr] == 1){
                    // nothing happen, don't buy at bad state, but never lose the opportunity of big fall
                } else
                /** check if too many days passed, or something bad happened among those waiting days */
                if(itr - targetIndex > 7 || KState.isSellDecisionStrong(k_state[itr])){
                    hasTarget = false;
                    sr_sig[itr] = NOTHING;
                } else {
                    newHighPrice = maxMinUtil.getMaxPrice(maxIndexShort[itr]);   // get current few days new high
                    newLowPrice = maxMinUtil.getMinPrice(minIndexShort[itr]);    // get current few days new low
                    sr_sig[itr] = prepareReason;
                    /** check if PREPARE_STEADY_UP retreat */
                    if(prepareReason == PREPARE_STEADY_UP){
                        if(//sr_sig[i - 1] == PREPARE_STEADY_UP &&
                                middleShadowPct[itr] < 0 && calcUtil.isVolumeDownEnough(itr)){
                            sr_sig[itr] = WILLBUY;
                        } //else sr_sig[i] = NOTHING;
                    } else if(prepareReason == PREPARE_NEW_HIGH && MathStockUtil.calculateChangePct(supportLine, newHighPrice) > 0.02){
                        /** check if PREPARE_NEW_HIGH retreat */
                        if(MathStockUtil.calculateChangePct(supportLine, newHighPrice) > 0.04
                                && Math.abs(MathStockUtil.calculateChangePct(supportLine, end[itr])) < 0.01){
                            sr_sig[itr] = WILLBUY;
                        } else if(downSerialCnt > 0 && downPctAccum < -0.01
                                && calcUtil.isVolumeDownEnough(itr)
                            //&& MathHelper.isInRange(MathStockUtil.calculateChangePct(supportLine, end[i]), -0.05, 0.04)
                                ){
                            sr_sig[itr] = WILLBUY;
                        }
                    } else if(prepareReason == PREPARE_MIN_SUPPORT_REVERSE){
                        /** not create new low, already reverse*/
                        if(changePct[itr] > 0 && MathStockUtil.calculateChangePct(supportLine, newLowPrice) > 0.01
                                && calcUtil.isVolumeDownEnough(itr)){
                            sr_sig[itr] = WILLBUY;
                        } else if(MathStockUtil.calculateChangePct(supportLine, newLowPrice) < -0.03
                                && changePct[itr] > 0
                                // TODO add volume constrain, not too little
                                //&& !calcUtil.isVolumeDownEnough(i)
                                ){
                            /** create new low, evolve to short trap */
                            sr_sig[itr] = WILLBUY;
                        }
                    } else if(prepareReason == PREPARE_MA_SUPPORT_REVERSE){
                        hasCloseEnough = checkIfCloseEnough(itr, true);
                        if(!hasCloseEnough) continue;   // if not close, don't decide
                        if(middleShadowPct[itr] < 0 && calcUtil.isVolumeUpEnough(itr)){
                            clearTarget(itr);
                        } else
                        /** not create new low, already reverse*/
                        if(changePct[itr] > 0
                                && MathStockUtil.calculateChangePct(pMaLong[itr], newLowPrice) > 0
                                && calcUtil.isVolumeDownEnough(itr)){
                            sr_sig[itr] = WILLBUY;
                        } else if(MathStockUtil.calculateChangePct(pMaLong[itr], newLowPrice) < -0.03
                                && end[itr] > pMaLong[itr]
                            // TODO add volume constrain, not too little
                            //&& !calcUtil.isVolumeDownEnough(i)
                                ){
                            /** create new low, evolve to short trap */
                            sr_sig[itr] = WILLBUY;
                        }
                    }
                }
            }
        }
    }

    private void recordPrepareSupportMax(int index, int reason){
        recordPrepareBase(index, reason);
        supportLine = maxMinUtil.getMaxPrice(maxIndexLong[index-1]);
    }

    private void recordPrepareSupportMin(int index, int reason){
        recordPrepareBase(index, reason);
        supportLine = maxMinUtil.getMaxPrice(minIndexLong[index-1]);
    }

    private void recordPrepareSupportMa(int index, int reason){
        recordPrepareBase(index, reason);
        supportLine = pMaLong[index];
        hasCloseEnough = checkIfCloseEnough(index, true);
    }

    private void recordPrepareBase(int index, int reason){
        sr_sig[index] = prepareReason = reason;
        hasTarget = true;
        targetIndex = index;
    }

    private void clearTarget(int index){
        sr_sig[index] = NOTHING;
        hasTarget = false;
        targetIndex = 0;
    }

    /**
     * isUpDown true, means check every day's min is close to pMA
     */
    private boolean checkIfCloseEnough(int index, boolean isUpDown){
        return isUpDown
                ? (MathStockUtil.calculateChangePct(pMaLong[index], min[index]) < 0.01)
                : (MathStockUtil.calculateChangePct(pMaLong[index], max[index]) > 0.00);
    }

    /**
     * consecutive days create new highs, and no crazy market condition
     */
    private boolean isNewHighsNoCrazy(int index){
        if(index > 2 && maxIndexLong[index] == index && maxIndexLong[index - 1] == index - 1
                && changePct[index -1] + changePct[index] < 0.05){
            int lowIndex = getLowestPriceIndex(index);
            double avgUpPct = (MathStockUtil.calculatePct(maxMinUtil.getMinPrice(lowIndex), end[index])) / (index - lowIndex + 1);
            double avgAmplitudeRatio = calcAvgAmplitude(lowIndex, index);
            if(avgUpPct > 0 && avgUpPct < 0.20 && avgAmplitudeRatio < 0.15){
                return true;
            }
//            if(middleShadowPct[index - 1] * middleShadowPct[index] < 0){
//                if(middleShadowPct[index - 1] < 0 && total[index - 1] < total[index]) {
//                    return true;
//                } else if(middleShadowPct[index] < 0 && total[index - 1] > total[index]) {
//                    return true;
//                }
//            } else if(middleShadowPct[index] > 0){  // make sure two days all red light
//                return true;
//            }
        }
        return false;
    }

    /**
     * use minIndexShort to find closest lowest price index
     */
    private int getLowestPriceIndex(int index){
        int previousLowIndex = minIndexShort[index];
        while (previousLowIndex > 0 && previousLowIndex != index){
            index = previousLowIndex;
            previousLowIndex = minIndexShort[index];
        }
        return previousLowIndex < 0 ? 0 : previousLowIndex;
    }

    /**
     * calculate given period time's average amplitude ratio
     * @param from
     * @param to
     * @return
     */
    private double calcAvgAmplitude(int from, int to){
        double amplitudeRatio = 0;
        for (int i = from; i <= to; i++) {
            amplitudeRatio += MathStockUtil.calculateChangePct(min[i], max[i]);
        }
        return amplitudeRatio / (from - to + 1);
    }

    @Override
    protected void initIndex() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        pMaLong = (double[])stock.queryCmpIndex("pMaLong");
        //volPriceCorr = (int[])stock.queryCmpIndex("vp_corr");
        k_state = (int[])stock.queryCmpIndex("k_state");
        if(filterUtil != null)
        isBad = (int[])filterUtil.getMarketIndex().queryCmpIndex("isBad");

        sr_sig = new int[len];
        calcUtil.init(stock);
        maxMinUtil.init(stock);
    }
}
