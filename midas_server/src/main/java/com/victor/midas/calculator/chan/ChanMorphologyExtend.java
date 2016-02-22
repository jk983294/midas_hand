package com.victor.midas.calculator.chan;

import com.victor.midas.calculator.chan.model.ChanStroke;
import com.victor.midas.calculator.chan.model.MergedKLine;
import com.victor.midas.calculator.chan.model.SupportResistLines;
import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.common.model.FractalType;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Price Moving Average Tangle Up state, like pMa5 > pMa10 > pMa20 > pMa30 > pMa60
 */
public class ChanMorphologyExtend extends IndexCalcBase {

    public static final String INDEX_NAME = "cme";

    private int[] cob;

    private double[] end, start, max, min, avgVolume, total, changePct;

    private double[] scores, cme;

    private int len;

    private List<MergedKLine> mergedKLines;

    private List<Integer> fractalKeyPoints;

    private List<ChanStroke> strokes;

    private SupportResistLines supportResistLines;

    public ChanMorphologyExtend(CalcParameter parameter) {
        super(parameter);
    }

    @Override
    public String getIndexName() {
        return INDEX_NAME;
    }

    @Override
    public void setRequiredCalculators() {
        requiredCalculators.add(IndexChangePct.INDEX_NAME);
    }

    @Override
    public void calculate() throws MidasException {
        mergedKLines = new ArrayList<>();
        fractalKeyPoints = new ArrayList<>();
        strokes = new ArrayList<>();
        supportResistLines = new SupportResistLines();
        for (int i = 0; i < len; i++) {
            mergeKLine(i);
            decideFractalType();
            scores[i] = supportResistLines.getScore(i, end[i]);
        }

        addIndexData(INDEX_NAME, scores);
    }

    /**
     * create K line, merge it into prev node, if could not merge, add it into list
     */
    private void mergeKLine(int i){
        MergedKLine current = new MergedKLine(i, i, cob[i], cob[i], max[i], min[i]);
        int length = mergedKLines.size();
        if(length >= 2){
            MergedKLine prevprev = mergedKLines.get(length - 2), prev = mergedKLines.get(length - 1);
            if( ! prev.merge(prevprev, current)){
                mergedKLines.add(current);
            }
        } else {
            mergedKLines.add(current);
        }
    }

    /**
     * decide which fractal type is, bottom or top
     */
    private void decideFractalType(){
        int length = mergedKLines.size();
        if(length >= 3){
            MergedKLine prevprev = mergedKLines.get(length - 3), prev = mergedKLines.get(length - 2), current = mergedKLines.get(length - 1);
            if(prev.decideFractalType(prevprev, current)){
                decideStroke(length - 2);
            }
        }
    }

    private void decideStroke(int currentIndex){
        if(fractalKeyPoints.size() == 0){
            fractalKeyPoints.add(currentIndex);
            updateSupportResist(ChanStroke.getChanStokeFromMergedKLines(mergedKLines.get(0), mergedKLines.get(currentIndex)), null);
        } else {
            int prevIndex = fractalKeyPoints.get(fractalKeyPoints.size() - 1);
            MergedKLine prev = mergedKLines.get(prevIndex), current = mergedKLines.get(currentIndex);
            if(prev.getType().equals(current.getType())){
                // same type, but if same top, current must higher than prev, or if same bottom, current must lower than prev
                if((FractalType.Top.equals(prev.getType()) && prev.getHigh() < current.getHigh())
                        || (FractalType.Bottom.equals(prev.getType()) && prev.getLow() > current.getLow())){
                    fractalKeyPoints.set(fractalKeyPoints.size() - 1, currentIndex);  // if both bottom or top, keep last one
                    updateSupportResist(strokes.get(strokes.size() - 1), mergedKLines.get(currentIndex));
                }
            } else {
                if(currentIndex - prevIndex >= 3 || current.height(prev) > 0.18){ // must be independent k line
                    fractalKeyPoints.add(currentIndex);
                    updateSupportResist(ChanStroke.getChanStokeFromMergedKLines(mergedKLines.get(prevIndex), mergedKLines.get(currentIndex)), null);
                }
            }
        }
    }

    /**
     *
     */
    private void updateSupportResist(ChanStroke stroke, MergedKLine mline){
        if(mline != null){  // update
            stroke.update(mline);
            supportResistLines.addStroke(stroke);
        } else {    // add new
            strokes.add(stroke);
            supportResistLines.addStroke(stroke);
        }
    }

    @Override
    protected void initIndex() throws MidasException {
        end = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_END);
        start = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_START);
        max = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MAX);
        min = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_MIN);
        total = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_TOTAL);
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        cob = stock.getDatesInt();

        len = end.length;
        scores = new double[len];
        cmpIndexName2Index = new HashMap<>();
    }

    public List<MergedKLine> getMergedKLines() {
        return mergedKLines;
    }

    public void setMergedKLines(List<MergedKLine> mergedKLines) {
        this.mergedKLines = mergedKLines;
    }

    public List<ChanStroke> getStrokes() {
        return strokes;
    }

    public void setStrokes(List<ChanStroke> strokes) {
        this.strokes = strokes;
    }

    public List<Integer> getFractalKeyPoints() {
        return fractalKeyPoints;
    }

    public void setFractalKeyPoints(List<Integer> fractalKeyPoints) {
        this.fractalKeyPoints = fractalKeyPoints;
    }
}
