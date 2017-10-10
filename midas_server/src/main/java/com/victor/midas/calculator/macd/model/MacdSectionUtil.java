package com.victor.midas.calculator.macd.model;

import com.victor.midas.calculator.common.model.SignalType;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * update macd sections
 */
public class MacdSectionUtil {

    public double[] min, max, end, macdBar;

    public List<Integer> points = new ArrayList<>();
    public List<MacdSection> sections = new ArrayList<>();
    public List<MacdSection> greenSections = new ArrayList<>();
    public List<MacdSection> redSections = new ArrayList<>();
    public LinkedList<MacdSection> overrideGreenSections = new LinkedList<>();
    public MacdSection lastSection;

    public void init(double[] min, double[] max, double[] end, double[] macdBar){
        this.min = min;
        this.max = max;
        this.end = end;
        this.macdBar = macdBar;
        lastSection = null;
        sections.clear();
        greenSections.clear();
        redSections.clear();
        overrideGreenSections.clear();
    }

    public void update(int i){
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

        if(lastSection != null && lastSection.signalType == SignalType.buy) {
            updateSectionDivergence();
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

    /**
     * find those divergence point where price is lower
     * those point will include green section's limit1 and limit3, red section limit2
     */
    public void updateSectionDivergence(){
        points.clear();
        if(CollectionUtils.isNotEmpty(greenSections) && greenSections.size() >= 3){
            double price = -100d;
            int index = -1;
            double barLength = -100d;
            for (int i = greenSections.size() - 1; i >= 1; i--) {
                MacdSection thisSection = greenSections.get(i);

                if(index < 0){     // not initialized
                    if(thisSection.limitIndex3 != -1){
                        index = thisSection.limitIndex3;
                        points.add(index);
                        price = min[thisSection.limitIndex3];
                        if(price < min[thisSection.limitIndex1]){
                            points.add(thisSection.limitIndex1);
                            price = min[thisSection.limitIndex1];
                        } else {
                            return;
                        }
                    } else if(thisSection.limitIndex1 != -1){
                        index = thisSection.limitIndex1;
                        points.add(index);
                        price = min[thisSection.limitIndex1];
                    }
                    barLength = thisSection.limit1;
                } else {            // initialized
                    if(barLength < thisSection.limit1) return;

                    if(thisSection.limitIndex3 != -1){
                        if(price < min[thisSection.limitIndex3]){
                            points.add(thisSection.limitIndex3);
                            price = min[thisSection.limitIndex3];
                        } else {
                            return;
                        }
                    }
                    if(thisSection.limitIndex1 != -1){
                        if(price < min[thisSection.limitIndex1]){
                            points.add(thisSection.limitIndex1);
                            price = min[thisSection.limitIndex1];
                        } else {
                            return;
                        }
                    }
                }
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

}
