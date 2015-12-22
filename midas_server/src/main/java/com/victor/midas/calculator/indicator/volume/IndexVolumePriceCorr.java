package com.victor.midas.calculator.indicator.volume;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.kline.IndexKLine;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.CalcUtil;
import com.victor.midas.model.common.VolPriceCorrelation;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.MathHelper;

import java.util.HashMap;

/**
 * calculate K line basic, compare with yesterday's end price
 */
public class IndexVolumePriceCorr extends IndexCalcBase {

    public final static String INDEX_NAME = "vp_corr";

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKLine.INDEX_NAME);
    }

    private final static int FREEZE_TIME_FRAME_FACTOR = 3;

    private CalcUtil calcUtil;

    private double[] changePct;
    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaShortD1;
    private double[] pD1;
    private double[] volRatio;


    private int[] signals;

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;

    private int len;

    public IndexVolumePriceCorr(CalcParameter parameter) {
        super(parameter);
        calcUtil = new CalcUtil();
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        VolPriceCorrelation corr = null;
        int frezzeDay = 0;
        for (int i = 1; i < len; i++) {
            corr = calcVolPriceCorrelation(i);
            if(VolPriceCorrelation.isBadHugeVolume(corr)){
                frezzeDay = Math.max(frezzeDay, MathHelper.multiplyReturnInt(FREEZE_TIME_FRAME_FACTOR, volRatio[i]));
            }
            if(frezzeDay > 0) {
                --frezzeDay;
                corr = VolPriceCorrelation.HUGE_VOLUME_FREZZE_TIME;
            }
            signals[i] = VolPriceCorrelation.signal(corr);
        }

        addIndexData(INDEX_NAME, signals);
    }

    private VolPriceCorrelation calcVolPriceCorrelation(int index){
        if(calcUtil.isExtremeBigVolume(index)){ return VolPriceCorrelation.HUGE_EXTREME_VOLUME; }
        if(calcUtil.isBigVolume(index) && calcUtil.isBigEntityDown(index)){
            return VolPriceCorrelation.HUGE_VOLUME_DOWN;
        }
        if(calcUtil.isBigVolume(index) && calcUtil.isBigEntityUp(index)){
            return  VolPriceCorrelation.HUGE_VOLUME_UP;
        }
        if(calcUtil.isVolumeUp(index)){
            return middleShadowPct[index] < 0 ? VolPriceCorrelation.PRICE_DOWN_VOL_UP : VolPriceCorrelation.PRICE_UP_VOL_UP;
        }
        if(calcUtil.isVolumeDown(index)){
            return middleShadowPct[index] < 0 ? VolPriceCorrelation.PRICE_DOWN_VOL_DOWN : VolPriceCorrelation.PRICE_UP_VOL_DOWN;
        }
        return VolPriceCorrelation.NO_MEANING;
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        pMaShortD1 = (double[])stock.queryCmpIndex("pMa5D1");
        pD1 = (double[])stock.queryCmpIndex("pD1");
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        volRatio = (double[])stock.queryCmpIndex("volRatio");

        len = end.length;
        signals = new int[len];
        cmpIndexName2Index = new HashMap<>();
        calcUtil.init(stock);
    }
}
