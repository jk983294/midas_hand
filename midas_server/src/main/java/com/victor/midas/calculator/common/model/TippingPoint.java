package com.victor.midas.calculator.common.model;


public class TippingPoint {

    public int cobIndex, arrayIndex;
    public double price;
    public FractalType type;

    public TippingPoint(int cobIndex, int arrayIndex, double price, FractalType type) {
        this.cobIndex = cobIndex;
        this.arrayIndex = arrayIndex;
        this.price = price;
        this.type = type;
    }

    public int getCobIndex() {
        return cobIndex;
    }

    public void setCobIndex(int cobIndex) {
        this.cobIndex = cobIndex;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public FractalType getType() {
        return type;
    }

    public void setType(FractalType type) {
        this.type = type;
    }
}
