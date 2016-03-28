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
import com.victor.midas.model.common.StockState;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.util.MidasException;
import com.victor.utilities.math.stats.ma.MaBase;
import com.victor.utilities.math.stats.ma.SMA;
import com.victor.utilities.utils.MathHelper;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
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

    private List<Integer> idxes = new ArrayList<>();
    private List<MacdSection> sections = new ArrayList<>();
    private List<MacdSection> greenSections = new ArrayList<>();
    private List<MacdSection> redSections = new ArrayList<>();
    private LinkedList<MacdSection> overrideGreenSections = new LinkedList<>();
    private MacdSection lastSection;
    private IndexLines lines;
    private IndexLine lastLine1, lastLine2, lastLine3;

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
        lastSection = null;
        lines = new IndexLines();
        sections.clear();
        greenSections.clear();
        redSections.clear();
        overrideGreenSections.clear();
        StockState state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20160122){
//                System.out.println("wow");
//            }
            updateStats(i);

            if(state == StockState.HoldMoney){
//                if(lastLine1 != null && lastLine2 != null && lastLine3 != null
//                        && lastSection.shouldHoldByStatus()
//                        && lastLine1.cnt == 2 && lastLine2.cnt < 7
//                        && MathHelper.isLessAbs(lastLine2.point2.value, lastLine2.point1.value, singleDouble)
//                        && lastLine1.point1.value > 0 && lastLine3.isLineCrossZeroUp()){
//                    score[i] = 1d;
//                    state = StockState.HoldStock;
//                }
                if(lastSection.type == MacdSectionType.green){
                    if(lastSection.signalType == SignalType.buy && greenSections.size() > 1){
                        updateGreenSectionDivergence(greenSections);
                        MacdSection preGreenSection = greenSections.get(greenSections.size() - 2);
                        if(idxes.size() >= 4 && lastSection.status == MacdSectionStatus.decay2 && MathHelper.isMoreAbs(preGreenSection.limit1, lastSection.limit1, 0.65)){
                            score[i] = 6d;
                            state = StockState.HoldStock;
                        }
                    }
                    if(lastSection.status == MacdSectionStatus.decay2 && lastSection.signalType == SignalType.buy
                            && end[i] < end[lastSection.limitIndex1] && Math.abs(macdBar[i]) < Math.abs(macdBar[lastSection.limitIndex1]) * 0.105){
                        score[i] = 7d;
                        state = StockState.HoldStock;
                    }
                    if(lastSection.signalType == SignalType.buy && changePct[i] < -0.075d){  // the first time when price still big fall, but bar arise
                        score[i] = 10d;
                        state = StockState.HoldStock;
                    }
                } else if(lastSection.type == MacdSectionType.red && greenSections.size() > 0 && redSections.size() > 1){
                    MacdSection lastGreen = greenSections.get(greenSections.size() - 1);
                    MacdSection lastRed = redSections.get(redSections.size() - 2);
                    if(lastSection.signalType == SignalType.buy && lastSection.status == MacdSectionStatus.grow2
                            && MathHelper.isLessAbs(lastSection.limit1, lastGreen.limit1, 0.45d)
                            && min[lastSection.limitIndex2] < lastGreen.pricelimit
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
        lastLine1 = lines.getLastLine(1);
        lastLine2 = lines.getLastLine(2);
        lastLine3 = lines.getLastLine(3);
        if(sections.size() == 0){
            lastSection = MacdSection.create(i, macdBar[i], end[i]);
            addSection();
        } else if(lastSection.update(i, macdBar[i], min[i], max[i])){
            updateOverride();
        } else {
            updateOverride();
            if(lastSection.type == MacdSectionType.green){
                overrideGreenSections.add(lastSection);
            }
            lastSection = MacdSection.create(i, macdBar[i], end[i]);
            addSection();
        }
    }

    /**
     * collect green section index
     */
    private void updateGreenSectionDivergence(List<MacdSection> greens){
        idxes.clear();
        if(CollectionUtils.isNotEmpty(greens) && greens.size() > 0){
            double price = 0d;
            MacdSection thisSection = greens.get(greens.size() - 1);
            if(thisSection.limitIndex3 != -1){
                idxes.add(thisSection.limitIndex3);
                price = min[thisSection.limitIndex3];
                if(price < min[thisSection.limitIndex1]){
                    idxes.add(thisSection.limitIndex1);
                    price = min[thisSection.limitIndex1];
                } else {
                    return;
                }
            } else if(thisSection.limitIndex1 != -1){
                idxes.add(thisSection.limitIndex1);
                price = min[thisSection.limitIndex1];
            }
            int skipCnt = 0;
            for (int i = greens.size() - 2; i >= 0; i--) {
                thisSection = greens.get(i);
                if(thisSection.limitIndex3 != -1){
                    if(price < min[thisSection.limitIndex3]){
                        idxes.add(thisSection.limitIndex3);
                        price = min[thisSection.limitIndex3];
                        skipCnt = 0;
                    } else {
                        skipCnt++;
                    }
                }
                if(thisSection.limitIndex1 != -1){
                    if(price < min[thisSection.limitIndex1]){
                        idxes.add(thisSection.limitIndex1);
                        price = min[thisSection.limitIndex1];
                        skipCnt = 0;
                    } else {
                        skipCnt++;
                    }
                }
                if(skipCnt >= 2) return;
            }
        }
    }

    private void addSection(){
        if(lastSection != null){
            sections.add(lastSection);
            if(lastSection.type == MacdSectionType.green){
                greenSections.add(lastSection);
            } else {
                redSections.add(lastSection);
            }
        }
    }

    private void updateOverride(){
        if(lastSection != null && lastSection.type == MacdSectionType.green && overrideGreenSections.size() > 0){
            int cnt = 0, overrideDirectCnt = 0;
            MacdSection toRemove = overrideGreenSections.peekLast();
            while (toRemove != null){
                if(Math.abs(lastSection.limit1) > Math.abs(toRemove.limit1)){
                    cnt += (toRemove.overrideCnt + 1);
                    overrideDirectCnt++;
                    overrideGreenSections.removeLast();
                    toRemove = overrideGreenSections.peekLast();
                } else break;
            }
            lastSection.overrideCnt += cnt;
            lastSection.overrideDirectCnt += overrideDirectCnt;
        }
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
