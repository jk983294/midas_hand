package com.victor.midas.calculator.macd;

import com.victor.midas.calculator.common.IndexCalcBase;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * calculate MACD
 */
public class IndexMacdAdvancedSignal extends IndexCalcBase {

    public static final String INDEX_NAME = "macd_advanced";

    private double[] dif, dea, macdBar; // white line, yellow line, bar
    private double[] score;

    private List<MacdSection> sections = new ArrayList<>();
    private List<MacdSection> greenSections = new ArrayList<>();
    private List<MacdSection> redSections = new ArrayList<>();
    private LinkedList<MacdSection> overrideGreenSections = new LinkedList<>();
    private MacdSection lastSection;

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
        lastSection = null;
        sections.clear();
        greenSections.clear();
        redSections.clear();
        overrideGreenSections.clear();
        StockState state = StockState.HoldMoney;
        for (int i = 5; i < len; i++) {
//            if(dates[i] == 20150818){
//                System.out.println("wow");
//            }
            if(sections.size() == 0){
                lastSection = MacdSection.create(i, macdBar[i]);
                addSection();
            } else if(lastSection.update(i, macdBar[i])){
                updateOverride();
            } else {
                updateOverride();
                overrideGreenSections.add(lastSection);
                lastSection = MacdSection.create(i, macdBar[i]);
                addSection();
            }

            if(state == StockState.HoldMoney && lastSection.signalType == SignalType.buy
                   && overrideGreenSections.size() > 4 ){  // || lastSection.overrideCnt > 0
                score[i] = 5d;
                state = StockState.HoldStock;
            } else if(state == StockState.HoldStock && lastSection.shouldSellByStatus()){
                score[i] = -5d;
                state = StockState.HoldMoney;
            }
//            score[i] = lastSection.status.ordinal();
        }
        addIndexData(INDEX_NAME, score);
    }

    private boolean isFirstBuyChanceForDangerSection(){ // || lastSection.overrideCnt > 0
        return (overrideGreenSections.size() == 0 ) && lastSection.status == MacdSectionStatus.decay1;
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
            int cnt = 0;
            MacdSection toRemove = overrideGreenSections.peekLast();
            while (toRemove != null){
                if(Math.abs(lastSection.limit1) > Math.abs(toRemove.limit1)){
                    cnt++;
                    overrideGreenSections.removeLast();
                    toRemove = overrideGreenSections.peekLast();
                } else break;
            }
            lastSection.overrideCnt = cnt;
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
