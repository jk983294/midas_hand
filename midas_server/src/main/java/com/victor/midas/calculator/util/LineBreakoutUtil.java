package com.victor.midas.calculator.util;

import com.victor.midas.calculator.macd.model.MacdSectionType;
import com.victor.midas.calculator.util.model.LineCrossSection;

import java.util.ArrayList;
import java.util.List;

/**
 * check if line value cross out
 */
public class LineBreakoutUtil {
    public double[] lineShort, lineLong;
    public int[] result;
    public int len;

    public List<LineCrossSection> sections = new ArrayList<>();
    public List<LineCrossSection> greenSections = new ArrayList<>();
    public List<LineCrossSection> redSections = new ArrayList<>();
    public LineCrossSection currentSection, previousSection;

    public void init(double[] lineShort, double[] lineLong){
        this.lineShort = lineShort;
        this.lineLong = lineLong;
        len = lineShort.length;
        sections.clear();
        greenSections.clear();
        redSections.clear();
        currentSection = previousSection = null;
    }

    public void update(int i){
        if(sections.size() == 0){
            LineCrossSection newSection = LineCrossSection.create(i, lineShort[i], lineLong[i]);
            addSection(newSection);
        } else if(currentSection.update(i, lineShort[i], lineLong[i])){
//            updateOverride();
        } else {
//            updateOverride();
            LineCrossSection newSection = LineCrossSection.create(i, lineShort[i], lineLong[i]);
            addSection(newSection);
        }
    }

    private void addSection(LineCrossSection newSection){
        previousSection = currentSection;
        currentSection = newSection;
        sections.add(currentSection);
        if(currentSection.type == MacdSectionType.green){
            greenSections.add(currentSection);
        } else {
            redSections.add(currentSection);
        }
    }

}
