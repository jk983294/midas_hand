package com.victor.midas.calculator.chan.model;


import com.victor.midas.calculator.common.model.FractalType;
import com.victor.midas.calculator.util.MathStockUtil;

public class MergedKLine {

    private int fromIndex, toIndex;

    private int fromCob, toCob;

    private double high, low;

    /**
     * exactly day the price make high or make low
     */
    private double highIndex, lowIndex;

    private FractalType type;

    public MergedKLine(int fromIndex, int toIndex, int fromCob, int toCob, double high, double low) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.fromCob = fromCob;
        this.toCob = toCob;
        this.high = high;
        this.low = low;
        this.highIndex = fromIndex;
        this.lowIndex = fromIndex;
    }

    /**
     * true means this one contains the other, need to merge to this, then discard the other
     * false means no contain relationship, no need to merge
     */
    public boolean merge(MergedKLine previous, MergedKLine next){
        if((this.high >= next.high && this.low <= next.low) || (this.high <= next.high && this.low >= next.low)){
            if(this.high > previous.high){  // up merge
                if(this.high < next.high){
                    this.high = next.high;
                    this.highIndex = next.highIndex;
                }
                if(this.low < next.low){
                    this.low = next.low;
                    this.lowIndex = next.lowIndex;
                }
            } else {    // down merge
                if(this.high > next.high){
                    this.high = next.high;
                    this.highIndex = next.highIndex;
                }
                if(this.low > next.low){
                    this.low = next.low;
                    this.lowIndex = next.lowIndex;
                }
            }
            this.toIndex = next.getToIndex();
            this.toCob = next.toCob;
            return true;
        }
        return false;
    }

    public boolean decideFractalType(MergedKLine previous, MergedKLine next){
        if(type == null){
            if(this.high > previous.high && this.high > next.high){
                this.type = FractalType.Top;
                return true;
            } else if(this.low < previous.low && this.low < next.low){
                this.type = FractalType.Bottom;
                return true;
            }
        }
        return false;
    }

    public double height(MergedKLine x){
        return Math.abs(MathStockUtil.calculateChangePct(Math.min(this.low, x.getLow()), Math.max(this.high, x.getHigh())));
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public FractalType getType() {
        return type;
    }

    public void setType(FractalType type) {
        this.type = type;
    }

    public int getFromCob() {
        return fromCob;
    }

    public void setFromCob(int fromCob) {
        this.fromCob = fromCob;
    }

    public int getToCob() {
        return toCob;
    }

    public void setToCob(int toCob) {
        this.toCob = toCob;
    }

    public double getHighIndex() {
        return highIndex;
    }

    public void setHighIndex(double highIndex) {
        this.highIndex = highIndex;
    }

    public double getLowIndex() {
        return lowIndex;
    }

    public void setLowIndex(double lowIndex) {
        this.lowIndex = lowIndex;
    }

    @Override
    public String toString() {
        return "MergedKLine{" +
                "fromIndex=" + fromIndex +
                ", toIndex=" + toIndex +
                ", fromCob=" + fromCob +
                ", toCob=" + toCob +
                ", high=" + high +
                ", low=" + low +
                ", highIndex=" + highIndex +
                ", lowIndex=" + lowIndex +
                ", type=" + type +
                '}';
    }
}
