package com.victor.midas.calculator.chan.model;

import java.util.List;

/**
 * results for chan
 */
public class ChanResults {

    private List<MergedKLine> mergedKLines;

    private List<Integer> fractalKeyPoints;

    private List<ChanStroke> strokes;

    public ChanResults() {
    }

    public ChanResults(List<MergedKLine> mergedKLines, List<Integer> fractalKeyPoints, List<ChanStroke> strokes) {
        this.mergedKLines = mergedKLines;
        this.fractalKeyPoints = fractalKeyPoints;
        this.strokes = strokes;
    }

    public List<MergedKLine> getMergedKLines() {
        return mergedKLines;
    }

    public void setMergedKLines(List<MergedKLine> mergedKLines) {
        this.mergedKLines = mergedKLines;
    }

    public List<Integer> getFractalKeyPoints() {
        return fractalKeyPoints;
    }

    public void setFractalKeyPoints(List<Integer> fractalKeyPoints) {
        this.fractalKeyPoints = fractalKeyPoints;
    }

    public List<ChanStroke> getStrokes() {
        return strokes;
    }

    public void setStrokes(List<ChanStroke> strokes) {
        this.strokes = strokes;
    }
}
