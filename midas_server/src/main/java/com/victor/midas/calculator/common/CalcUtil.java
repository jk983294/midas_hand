package com.victor.midas.calculator.common;

import com.victor.midas.model.common.KState;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

/**
 * calculate util for specified stock, base functionality, will be extended by specified strategy calc logic
 */
public class CalcUtil {

    protected StockVo stock;
    protected double[] end, start, max, min, total, changePct;
    protected double[] vMa;
    protected double[] volRatio;
    protected double[] upShadowPct, downShadowPct, middleShadowPct;
    protected double[] pMa5D1, pMa10D1, pMa20D1, pMa30D1, pMa60D1;
    protected double[] pMaLong;
    protected double[] trendPreVolRatio;
    protected double[] trendPrePreVolRatio;

    protected int[] ctf0;
    protected int[] k_state;

    public CalcUtil(){
    }

    public CalcUtil(StockVo stock) throws MidasException {
        init(stock);
    }

    public void init(StockVo stock) throws MidasException {
        this.stock = stock;
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        upShadowPct = stock.queryCmpIndexDoubleWithNull("k_u");
        downShadowPct = stock.queryCmpIndexDoubleWithNull("k_d");
        middleShadowPct = stock.queryCmpIndexDoubleWithNull("k_m");
        changePct = stock.queryCmpIndexDoubleWithNull(MidasConstants.INDEX_NAME_CHANGE_PCT);
        pMa5D1 = stock.queryCmpIndexDoubleWithNull("pMa5D1");
        pMa10D1 = stock.queryCmpIndexDoubleWithNull("pMa10D1");
        pMa20D1 = stock.queryCmpIndexDoubleWithNull("pMa20D1");
        pMa60D1 = stock.queryCmpIndexDoubleWithNull("pMa60D1");
        pMa30D1 = stock.queryCmpIndexDoubleWithNull("pMa30D1");
        pMaLong = stock.queryCmpIndexDoubleWithNull("pMaLong");

        vMa = stock.queryCmpIndexDoubleWithNull("vMaMedium");
        volRatio = stock.queryCmpIndexDoubleWithNull("volRatio");

        trendPreVolRatio = stock.queryCmpIndexDoubleWithNull("tpvr");
        trendPrePreVolRatio = stock.queryCmpIndexDoubleWithNull("tppvr");

        ctf0 = stock.queryCmpIndexIntWithNull("ctf0");
        k_state = stock.queryCmpIndexIntWithNull("k_state");
    }

    public double maxEntity(int index){ return Math.max(end[index], start[index]); }

    public double minEntity(int index){ return Math.min(end[index], start[index]); }

    public boolean isSmallEntity(int index){
        return Math.abs(middleShadowPct[index]) < 0.02;
    }

    public boolean isBigEntity(int index){
        return Math.abs(middleShadowPct[index]) > 0.05;
    }

    public boolean isBigEntityUp(int index){
        return (middleShadowPct[index] > 0.0) && isBigEntity(index);
    }

    public boolean isBigEntityDown(int index){
        return (middleShadowPct[index] < 0.0) && isBigEntity(index);
    }

    public boolean isMediumEntity(int index){
        return Math.abs(middleShadowPct[index]) > 0.03;
    }

    public boolean isMediumEntityUp(int index){
        return (middleShadowPct[index] > 0.0) && isMediumEntity(index);
    }

    public boolean isMediumEntityDown(int index){
        return (middleShadowPct[index] < 0.0) && isMediumEntity(index);
    }

    public boolean isLongUpShadow(int index){
        return upShadowPct[index] > 0.04;
    }

    public boolean isLongDownShadow(int index){
        return downShadowPct[index] > 0.05;
    }

    public boolean isLongUpShadowWeak(int index){
        return upShadowPct[index] > Math.max(0.02, 2 * (downShadowPct[index] + Math.abs(middleShadowPct[index])));
    }

    public boolean isLongDownShadowWeak(int index){
        return downShadowPct[index] > Math.max(0.02, 2 * (upShadowPct[index] + Math.abs(middleShadowPct[index])));
    }

    public boolean isLongShadowWeak(int index){
        return isLongUpShadowWeak(index) || isLongDownShadowWeak(index);
    }

    public boolean isLittleDownShadow(int index){
        return downShadowPct[index] < 0.005;
    }

    public boolean isChangePctBigFall(int index){
        return changePct[index] < -0.03;
    }

    public boolean isExtremeBigVolume(int index){
        return volRatio[index] > 4.5;
    }

    public boolean isBigVolume(int index){
        return volRatio[index] > 3;
    }

    public boolean isVolumeUp(int index){
        return volRatio[index] > 1.3;
    }

    public boolean isVolumeDown(int index){
        return volRatio[index] < 0.7;
    }

    public boolean isTrendUp(int index){
        return (pMa5D1[index] > 0) && (pMa10D1[index] > 0)  && (pMa30D1[index] > 0)
                && (pMa20D1[index] > 0) && (pMa60D1[index] > 0);
    }

    public boolean isDoji(int index){
        return Math.abs(middleShadowPct[index]) < 0.01;
    }

    public boolean isHighWave(int index){
        return (upShadowPct[index] + downShadowPct[index]) > 2 * Math.max(0.2, Math.abs(middleShadowPct[index]));
    }

    public boolean isPriceUpVolumeDown(int index){
        int tieIndex1 = firstTieNodeIndex(index);
        return (index - tieIndex1 > 1 && changePct[index] > 0 && trendPreVolRatio[index] < 0.65);
    }

    public boolean isVolumeDownTooMuchAgainstPreBuy(int index){
        return trendPrePreVolRatio[index] < 0.75;
    }

    protected int firstTieNodeIndex(int index){
        int tieIndex1;
        // find first tie node
        for (tieIndex1 = index - 1; tieIndex1 >= 0 ; --tieIndex1) {
            if(ctf0[tieIndex1] >= 0) break;
        }
        return tieIndex1 >= 0 ? tieIndex1 : 0;
    }

    protected boolean hasKStateSell(int from, int to){
        for (int i = from; i <= to; i++) {
            if(KState.isSellDecision(k_state[i])) return true;
        }
        return false;
    }

    protected double accumulateChangePct(int fromPos, int endPos){
        double result = 0;
        for (int i = fromPos; i <= endPos; i++) {
            result += changePct[i];
        }
        return result;
    }

    protected double avgVolume(int fromPos, int endPos){
        double result = 0;
        for (int i = fromPos; i <= endPos; i++) {
            result += total[i];
        }
        return result/( endPos - fromPos + 1);
    }
}
