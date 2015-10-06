package com.victor.midas.calculator.chartpattern;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.calculator.common.CalcUtil;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * based on Financial Time Series Segmentation Based On Turning Points, Jiangling Yin, Yain-Whar Si, Zhiguo Gong
 */
public class ChartTimeFrameWithVolume extends IndexCalcbase {

    private static final String INDEX_NAME = "ctf";

    private static final int UP = -1;
    private static final int TIE = -2;
    private static final int DOWN = -3;

    private double[] end;
    private double[] start;
    private double[] max;
    private double[] min;
    private double[] total;
    private double[] changePct;

    private double[] trendAvgVol;
    private double[] trendPreVolRatio;
    private double[] trendPrePreVolRatio;
    private int[] ctf;

    int len;

    private CalcUtil calcUtil;

    public ChartTimeFrameWithVolume(CalcParameter parameter) {
        super(parameter);
        calcUtil = new CalcUtil();
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    protected void calculateFromScratch() throws MidasException {
        ArrayList<Integer> ips = calculateInitialTP();
        //ips = turningPoints(ips);
        buildTrendArray(ctf, ips);
        addIndexData("ctf0", ctf);
        //addIndexData("tav", trendAvgVol);
        addIndexData("tpvr", trendPreVolRatio);
        addIndexData("tppvr", trendPrePreVolRatio);
    }

    @Override
    protected void calculateFromExisting() throws MidasException {
        calculateFromScratch();
    }

    @Override
    protected void calculateForTrain() throws MidasException {
        // TODO add support for ctf
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        len = end.length;

        trendAvgVol = new double[len];
        trendPreVolRatio = new double[len];
        trendPrePreVolRatio = new double[len];
        ctf = new int[len];

        cmpIndexName2Index = new HashMap<>();
        calcUtil.init(stock);
    }

    @Override
    protected void initIndexForTrain() throws MidasException {

    }

    @Override
    public void applyParameter() {

    }

    /**
     * calculate turning point, maximum and minimum point of certain period
     */
    private ArrayList<Integer> calculateInitialTP(){
        ArrayList<Integer> list = new ArrayList<>();
        /*** initial run to get local minimum or maximum point */
        list.add(0);
        for (int i = 1; i < len; i++) {
            if( i - 1 < 0) list.add(i);             //first day
            else if( i + 1 >= len) list.add(i);     //last day
            // yesterday and tomorrow are not same trend, then today should be consider tie
            else if( ! MathHelper.isSameSign(changePct[i], changePct[i+1])  ){
                list.add(i);
            }
        }
        // add last day as IP
        if(list.get(list.size() - 1) != len - 1){
            list.add(len - 1);
        }
        return list;
    }

    /**
     * one iterate for calculating higher level turning points list
     */
    private ArrayList<Integer> turningPoints(ArrayList<Integer> lowLevel){
        double changePctHeadAndTail, changePctMiddle, trend1, trend2;
        ArrayList<Integer> highLevel = new ArrayList<>();
        highLevel.add(lowLevel.get(0));
        int d1, d2, d3, d4, len = lowLevel.size(), i;
        for (i = 1; i < len - 3; ++i) {
            d1 = highLevel.get(highLevel.size() - 1);   // get last high level
            d2 = lowLevel.get(i);
            d3 = lowLevel.get(i+1);
            d4 = lowLevel.get(i+2);

            if(d3- d2 == 1 && d2 - d1 > 1 && d4- d3 > 1){
                trend1 = MathStockUtil.calculateChangePct(end[d1],end[d2]);
                trend2 = MathStockUtil.calculateChangePct(end[d3],end[d4]);
                if(MathHelper.isSameSignStrong(trend1, trend2)) {
                    changePctHeadAndTail = MathStockUtil.calculateChangePct(Math.min(calcUtil.minEntity(d1), calcUtil.minEntity(d4)), Math.max(calcUtil.maxEntity(d1), calcUtil.maxEntity(d4)));
                    changePctMiddle = MathStockUtil.calculateChangePct(Math.min(calcUtil.minEntity(d2), calcUtil.minEntity(d3)), Math.max(calcUtil.maxEntity(d2), calcUtil.maxEntity(d3)));
                    if(Math.abs(changePctMiddle / changePctHeadAndTail) < 0.2){
                        i += 1;
                        continue;
                    }
                }
            }
            highLevel.add(d2);
        }
        // add last un-used points
        for (; i < len ; ++i) {
            highLevel.add(lowLevel.get(i));
        }
        return highLevel;
    }

    /**
     * build trend array from important points
     */
    private void buildTrendArray(int[] trend, ArrayList<Integer> tps){
        int previousDayIndex, dayIndex;
        int length = tps.size();
        double totalVol;
        for (int i = 1; i < length; ++i) {
            previousDayIndex = tps.get(i-1);
            dayIndex = tps.get(i);
            if( i > 1) ++previousDayIndex;

            if(end[previousDayIndex] < end[dayIndex]){
                ArrayHelper.fill(trend, previousDayIndex, dayIndex, UP);
            } else {
                ArrayHelper.fill(trend, previousDayIndex, dayIndex, DOWN);
            }

            totalVol = 0;
            for(int j = previousDayIndex; j <= dayIndex; ++j){
                totalVol += total[j];
                trendAvgVol[j] = totalVol / ( j - previousDayIndex + 1);
                if( i > 1){ // current trend avg volume / previous trend avg volume
                    trendPreVolRatio[j] = trendAvgVol[j] / trendAvgVol[tps.get(i-1)];
                }
                if( i > 2){ // current trend avg volume / pre-previous trend avg volume
                    trendPrePreVolRatio[j] = trendAvgVol[j] / trendAvgVol[tps.get(i-2)];
                }
            }
        }

        for (int i = 1; i < length; ++i) {
            trend[tps.get(i)] = tps.get(i - 1);
            //trend[tps.get(i)] = TIE;
        }

        /*** set first tps point to itself */
        if(length > 2){
            trend[tps.get(0)] = 0;
        }
    }
}
