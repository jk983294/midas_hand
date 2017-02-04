package com.victor.utilities.model;


public class SimpleStatisticObject {

    public double key, mean, std;

    public SimpleStatisticObject(double key, double mean, double std) {
        this.key = key;
        this.mean = mean;
        this.std = std;
    }

    public SimpleStatisticObject() {
    }

    public double getKey() {
        return key;
    }

    public void setKey(double key) {
        this.key = key;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    @Override
    public String toString() {
        return "SimpleStatisticObject{" +
                "key=" + key +
                ", mean=" + mean +
                ", std=" + std +
                '}';
    }
}
