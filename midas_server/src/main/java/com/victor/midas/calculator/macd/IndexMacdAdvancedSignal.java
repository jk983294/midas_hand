package com.victor.midas.calculator.macd;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.common.model.IndexLine;
import com.victor.midas.calculator.common.model.IndexLines;
import com.victor.midas.calculator.common.model.SignalType;
import com.victor.midas.calculator.divergence.IndexBadDepth;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.macd.model.MacdSection;
import com.victor.midas.calculator.macd.model.MacdSectionStatus;
import com.victor.midas.calculator.macd.model.MacdSectionType;
import com.victor.midas.calculator.macd.model.MacdSectionUtil;
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import com.victor.utilities.utils.MathHelper;
import java.util.LinkedList;
import java.util.List;

/**
 * calculate MACD
 */
public class IndexMacdAdvancedSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "macd_advanced";
    private MaBase maMethod = new SMA();

    private double[] dif, dea, macdBar; // white line, yellow line, bar
    private double[] score;
    private double[] vMa5, pMa60;

    private List<Integer> points;
    private List<MacdSection> sections;
    private List<MacdSection> greenSections;
    private List<MacdSection> redSections;
    private LinkedList<MacdSection> overrideGreenSections;
    private MacdSection lastSection;
    private IndexLines lines;

    private MacdSectionUtil macdSectionUtil = new MacdSectionUtil();

    public IndexMacdAdvancedSignal(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
        requiredCalculators.add(IndexBadDepth.INDEX_NAME);
        requiredCalculators.add(IndexMACD.INDEX_NAME);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void calculate() throws MidasException {
        vMa5 = maMethod.calculate(total, 5);
        pMa60 = maMethod.calculate(end, 60);
        macdSectionUtil.init(min, max, end, macdBar);
        lastSection = macdSectionUtil.lastSection;
        sections = macdSectionUtil.sections;
        greenSections = macdSectionUtil.greenSections;
        redSections = macdSectionUtil.redSections;
        overrideGreenSections = macdSectionUtil.overrideGreenSections;
        points = macdSectionUtil.points;

        lines = new IndexLines();

        StockState state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20160122){
//                System.out.println("wow");
//            }
            updateStats(i);
            macdSectionUtil.update(i);
            lastSection = macdSectionUtil.lastSection;

            if(state == StockState.HoldMoney && lastSection.signalType == SignalType.buy){
                if(lastSection.type == MacdSectionType.green){
                    if(greenSections.size() > 1){
                        //macdSectionUtil.updateGreenSectionDivergence(greenSections);
                        MacdSection preGreenSection = greenSections.get(greenSections.size() - 2);
                        if(points.size() >= 4 && lastSection.status == MacdSectionStatus.decay2
                                && MathHelper.isMoreAbs(preGreenSection.limit1, lastSection.limit1, 0.65)){
                            score[i] = 6d;
                            state = StockState.HoldStock;
                        }
                    }
                    if(lastSection.status == MacdSectionStatus.decay2 && end[i] < end[lastSection.limitIndex1]
                            && Math.abs(macdBar[i]) < Math.abs(macdBar[lastSection.limitIndex1]) * 0.105){
                        score[i] = 7d;
                        state = StockState.HoldStock;
                    }
                    if(lastSection.signalType == SignalType.buy && changePct[i] < -0.075d){  // the first time when price still big fall, but bar arise
                        score[i] = 10d;
                        state = StockState.HoldStock;
                    }
                } else if(lastSection.type == MacdSectionType.red && greenSections.size() > 0 && redSections.size() > 1){
                    MacdSection lastGreen = greenSections.get(greenSections.size() - 1);
                    if(lastSection.status == MacdSectionStatus.grow2
                            && MathHelper.isLessAbs(lastSection.limit1, lastGreen.limit1, 0.45d)
                            && min[lastSection.limitIndex2] < lastGreen.priceLimit
                            && changePct[lastSection.limitIndex2] < -0.06d){  // the first time when price still big fall, but bar arise
                        score[i] = 9d;
                        state = StockState.HoldStock;
                    }
                }
            } else if(state == StockState.HoldStock && lastSection.shouldSellByStatus()){
                score[i] = -5d;
                state = StockState.HoldMoney;
            }
//            score[i] = lastSection.status1.ordinal();
        }
        addIndexData(INDEX_NAME, score);
    }

    private void updateStats(int i){
        lines.update(i, dif[i]);
    }

    @Override
    protected void initIndex() throws MidasException {
        dif = (double[])stock.queryCmpIndex("dif");
        dea = (double[])stock.queryCmpIndex("dea");
        macdBar = (double[])stock.queryCmpIndex("macdBar");
        score = new double[len];
    }

    @Override
    public MidasTrainOptions getTrainOptions() {
        MidasTrainOptions options = new MidasTrainOptions();
        options.selectTops = false;
        options.useSignal = true;
        return options;
    }
}
