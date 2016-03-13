package com.victor.midas.calculator.macd.model;


import com.victor.midas.calculator.common.model.FractalType;

public class TippingPoint {

    public int index;
    public double value;
    public FractalType type;

    public TippingPoint(int index, double value, FractalType type) {
        this.index = index;
        this.value = value;
        this.type = type;
    }

    public void copy(TippingPoint point){
        index = point.index;
        value = point.value;
        type = point.type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public FractalType getType() {
        return type;
    }

    public void setType(FractalType type) {
        this.type = type;
    }
}
