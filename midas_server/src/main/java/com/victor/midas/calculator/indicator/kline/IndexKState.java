package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.CalcUtil;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.KState;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.MathHelper;

import java.util.HashMap;

/**
 * calculate K line basic, compare with yesterday's end price
 */
public class IndexKState extends IndexCalcBase {

    public final static String INDEX_NAME = "k_state";

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
    }

    private CalcUtil calcUtil;

    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaShortD1;
    private double[] pD1;

    private int[] signals;

    public IndexKState(CalcParameter parameter) {
        super(parameter);
        calcUtil = new CalcUtil();
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        for (itr = 1; itr < len; itr++) {
            signals[itr] = KState.signal(calcKState(itr));
        }

        addIndexData(INDEX_NAME, signals);
    }

    private KState calcKState(int index){
        if(calcUtil.isSmallEntity(index)){
            if(calcUtil.isLongDownShadow(index)){
                if(pD1[index] > 0) return KState.HANGING_MAN;
                else if(pD1[index] < 0) return KState.HAMMER;
            }
            else if(calcUtil.isChangePctBigFall(index) && pD1[index] < 0 && middleShadowPct[index] > 0
                    && calcUtil.isLongUpShadow(index) && calcUtil.isLittleDownShadow(index)  ) return KState.INVERTED_HAMMER;
        }
        if(calcUtil.isDoji(index)){
            if(middleShadowPct[index - 1] > 0 && calcUtil.isLongUpShadow(index)) return KState.LONG_LEG_DOJI_DOWN;
            else if(middleShadowPct[index - 1] < 0 && calcUtil.isLongDownShadow(index)) return KState.LONG_LEG_DOJI_UP;
            if(calcUtil.isBigEntityUp(index - 1) && Math.max(end[index], start[index]) < end[index - 1]) return KState.HARAMI_CROSS_DOWN;
            else if(calcUtil.isBigEntityDown(index - 1) && Math.min(end[index], start[index]) > end[index - 1]) return KState.HARAMI_CROSS_UP;
        }
        if((calcUtil.isHighWave(index) && calcUtil.isHighWave(index - 1))
                || (calcUtil.isLongShadowWeak(index) && calcUtil.isLongShadowWeak(index - 1))){
            if(pD1[index - 1] > 0) return KState.HIGH_WAVE_DOWN;
            else return KState.HIGH_WAVE_UP;
        }
        if(calcUtil.isBigEntityUp(index)){
            if(middleShadowPct[index-1] < -0.03 &&  MathHelper.isInRange(end[index-1], start[index-1], end[index], start[index]))
                return KState.ENGULG_UP;
        }
        if(calcUtil.isBigEntityDown(index)){
            if(MathHelper.isInRange(end[index-1], start[index-1], end[index], start[index])) return KState.ENGULG_DOWN;
        }
        if(calcUtil.isBigEntityUp(index - 1)){
            if(middleShadowPct[index] < -0.03 &&  end[index] < MathHelper.average(end[index-1], start[index-1]))
                return KState.DARK_CLOUD_COVER;
        }
        if(calcUtil.isBigEntityDown(index - 1)){
            if(middleShadowPct[index] > 0.03 &&  end[index] > MathHelper.average(end[index-1], start[index-1]))
                return KState.PIERCING_PATTERN;
        }
        if(calcUtil.isBigEntity(index - 1) && calcUtil.isSmallEntity(index)){
            if(middleShadowPct[index - 1] > 0 && middleShadowPct[index] < 0
                    && Math.min(start[index], end[index]) >= end[index - 1]) return KState.EVENING_STAR;
            else if(middleShadowPct[index - 1] < 0 && middleShadowPct[index] > 0
                    && Math.max(start[index], end[index]) <= end[index - 1]) return KState.MORNING_STAR;
        }
        if(MathHelper.isInRange(MathStockUtil.calculateChangePct(max[index - 1], min[index]), 0.01, 0.03)){
            return KState.WINDOW_UP;
        }
        if(MathHelper.isInRange(MathStockUtil.calculateChangePct(min[index - 1], max[index]), -0.03, -0.01)){
            return KState.WINDOW_DOWN;
        }
        return KState.NO_MEANING;
    }

    @Override
    protected void initIndex() throws MidasException {
        pMaShortD1 = (double[])stock.queryCmpIndex("pMa5D1");
        pD1 = (double[])stock.queryCmpIndex("pD1");
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        signals = new int[len];
        calcUtil.init(stock);
    }
}
