package com.victor.midas.calculator.chan.model;

/**
 * one stroke is from top fractal to bottom or vice verse
 */
public class ChanStroke {

    private int fromIndex, toIndex;

    private int fromCob, toCob;

    private double high, low;

    /**
     * exactly day the price make high or make low
     */
    private double highIndex, lowIndex;

    private DirectionType type;

    public ChanStroke() {
    }

    public void update(MergedKLine next){
        this.toCob = next.getToCob();
        this.toIndex = next.getToIndex();
        if(this.high < next.getHigh()){
            this.high = next.getHigh();
            this.highIndex = next.getHighIndex();
        }
        if(this.low > next.getLow()){
            this.low = next.getLow();
            this.lowIndex = next.getLowIndex();
        }
    }

    public static ChanStroke getChanStokeFromMergedKLines(MergedKLine previous, MergedKLine next){
        if(isSameType(previous, next)){
            return null;
        }
        ChanStroke stroke = new ChanStroke();
        stroke.setFromCob(previous.getFromCob());
        stroke.setToCob(next.getToCob());
        stroke.setFromIndex(previous.getFromIndex());
        stroke.setToIndex(next.getToIndex());
        if(previous.getHigh() > next.getHigh()){
            stroke.setHigh(previous.getHigh());
            stroke.setHighIndex(previous.getHighIndex());
        } else {
            stroke.setHigh(next.getHigh());
            stroke.setHighIndex(next.getHighIndex());
        }
        if(previous.getLow() < next.getLow()){
            stroke.setLow(previous.getLow());
            stroke.setLowIndex(previous.getLowIndex());
        } else {
            stroke.setLow(next.getLow());
            stroke.setLowIndex(next.getLowIndex());
        }
        if(previous.getType() == null){
            stroke.setType( next.getType() == FractalType.Top ? DirectionType.Up : DirectionType.Down);
        } else {
            stroke.setType( previous.getType() == FractalType.Top ? DirectionType.Down : DirectionType.Up);
        }
        return stroke;
    }

    public static boolean isSameType(MergedKLine previous, MergedKLine next){
        if(previous.getType() == null && next.getType() == null){
            return true;
        } else if(previous.getType() == null || next.getType() == null){
            return false;
        } else {
            return previous.getType().equals(next.getType());
        }

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

    public DirectionType getType() {
        return type;
    }

    public void setType(DirectionType type) {
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
        return "ChanStroke{" +
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
