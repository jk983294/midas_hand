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

    public List<Integer> idxes = new ArrayList<>();
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

        if(lastSection != null && lastSection.type == MacdSectionType.green) {
            if (lastSection.signalType == SignalType.buy && greenSections.size() > 1) {
                updateGreenSectionDivergence(greenSections);
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

    /**
     * collect green section index
     */
    public void updateGreenSectionDivergence(List<MacdSection> greens){
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
