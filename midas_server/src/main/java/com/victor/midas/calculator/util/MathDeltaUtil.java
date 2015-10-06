package com.victor.midas.calculator.util;

import com.victor.utilities.utils.ArrayHelper;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * calculate delta
 */
public class MathDeltaUtil {

    private int timeFrame;

    private double[][] regressionData;

    private double[] data;

    public MathDeltaUtil(int timeFrame) {
        this.timeFrame = timeFrame;
        regressionData = prepareRegressionData();
    }

    /**
     * calculate delta
     */
    public double[] calculate(int frame, double[] a, boolean isPct){
        if(frame != timeFrame){
            timeFrame = frame;
            regressionData = prepareRegressionData();
        }
        data = a;
        return isPct ? calculateWithPercentage() : calculate();
    }

    public double[] calculate(double[] a, boolean isPct){
        data = a;
        return isPct ? calculateWithPercentage() : calculate();
    }


    public double[] calculate(){
        if(ArrayHelper.isNull(data) || timeFrame < 3) return null;
        int length = data.length;
        SimpleRegression regression = new SimpleRegression();
        double[] delta = new double[length];
        for (int i = timeFrame - 1; i < length; i++) {
            fillRegressionData(i);
            regression.addData(regressionData);
            delta[i] = regression.getSlope();
            regression.clear();
        }
        return delta;
    }

    public double[] calculateWithPercentage(){
        if(ArrayHelper.isNull(data) || timeFrame < 3) return null;
        int length = data.length;
        SimpleRegression regression = new SimpleRegression();
        double[] delta = new double[length];
        for (int i = timeFrame - 1; i < length; i++) {
            fillRegressionDataWithPercentage(i);
            regression.addData(regressionData);
            delta[i] = regression.getSlope();
            regression.clear();
        }
        return delta;
    }

    private double[][] prepareRegressionData(){
        double[][] regressionData = new double[timeFrame][2];
        for (int i = 0; i < regressionData.length; i++) {
            regressionData[i][0] = i;
        }
        return regressionData;
    }

    /**
     * use data[index - timeFramePriceDelta] ~ data[index - timeFramePriceDelta] as D0
     */
    private void fillRegressionData(int index){
        for (int i = 0; i < regressionData.length; i++) {
            regressionData[i][1] = data[index - timeFrame + i + 1];
        }
    }

    private void fillRegressionDataWithPercentage(int index){
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < regressionData.length; i++) {
            regressionData[i][1] = data[index - timeFrame + i + 1];
            minValue = Math.min(minValue, data[index - timeFrame + i + 1]);
        }
        // scale with minValue
        for (int i = 0; i < regressionData.length; i++) {
            regressionData[i][1] = MathStockUtil.calculateChangePct(minValue, regressionData[i][1]) * 100.0;
        }
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public int getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(int timeFrame) {
        this.timeFrame = timeFrame;
    }
}
