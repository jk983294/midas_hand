package com.victor.midas.calculator.indicator.kline;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.util.IndexFactory;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.CalcUtil;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.KState;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.TopKElements;
import org.apache.commons.math3.stat.StatUtils;

import java.util.HashMap;

/**
 * calculate K line basic, compare with yesterday's end price
 */
public class IndexLongGoodTrend extends IndexCalcBase {

    private final static String INDEX_NAME = "lgt";

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexKState.INDEX_NAME);
    }

    private CalcUtil calcUtil;

    private final static int LGT_TIME_FRAME = 10;

    private int[] lgt;      // long good trend
    private int[] k_state;

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;
    private double[] total;
    private double[] changePct;
    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] vMa;

    private int len;
    private int totalCnt;
    private int smallEntityCnt, bigEntityCnt;
    private int upEntityCnt, downEntityCnt;
    private int lgtValue;
    private double avgUpVolume, avgDownVolume;

    public IndexLongGoodTrend(CalcParameter parameter) {
        super(parameter);
        calcUtil = new CalcUtil();
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        for (int i = 1; i < len; i++) {
            lgt[i] = calcLgt(i);
        }

        addIndexData(INDEX_NAME, lgt);
    }

    private int calcLgt(int index){
        if(index < LGT_TIME_FRAME ) return 0;
        smallEntityCnt = bigEntityCnt = upEntityCnt = downEntityCnt = totalCnt = lgtValue = 0;
        avgUpVolume = avgDownVolume = 0.0;
        for (int i = index; i >= 0; --i) {
            if(changePct[i] < -0.06 || KState.isSellDecision(k_state[i])){ break; }
            ++totalCnt;
            if(Math.abs(changePct[i]) < 0.02) { ++smallEntityCnt; } else { ++bigEntityCnt;}
            if(middleShadowPct[i] > 0.0) {
                ++upEntityCnt;
            } else {
                ++downEntityCnt;
                avgDownVolume = (avgDownVolume * ( downEntityCnt - 1) + total[i]) / downEntityCnt;
            }

            if(totalCnt >= LGT_TIME_FRAME){
                if(MathStockUtil.calculatePct(smallEntityCnt, totalCnt) < 0.8
                        || MathStockUtil.calculatePct(upEntityCnt, totalCnt) < 0.8
                        || calcAvgUpVolume(index) < avgDownVolume
                        || MathStockUtil.calculateChangePct(end[index - totalCnt + 1], end[index]) > 0.012 * totalCnt){
                    lgtValue = totalCnt - LGT_TIME_FRAME;
                    break;
                }
            }
        }
        /**
         * take care, only signal in up trend
         */
        if(calcUtil.isTrendUp(index)){
            return lgtValue;
        } else {
            return 0;
        }
    }

    /**
     * fetch equal number of down entity, but max volume
     */
    private double calcAvgUpVolume(int index){
        if(downEntityCnt <= 0 || downEntityCnt > upEntityCnt) return 0.0;
        double[] data = new double[upEntityCnt];
        int findCnt = 0;
        for (int i = index; i > index - totalCnt; --i) {
            if(middleShadowPct[i] > 0.0) {
                data[findCnt++] = total[i];
            }
        }
        return StatUtils.mean(TopKElements.getFirstK(data, downEntityCnt));
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        vMa = (double[])stock.queryCmpIndex("vMaMedium");
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        k_state = (int[])stock.queryCmpIndex("k_state");
        len = end.length;

        lgt = new int[len];
        cmpIndexName2Index = new HashMap<>();
        calcUtil.init(stock);
    }
}
