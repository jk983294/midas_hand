package com.victor.midas.calculator.common.model;

/**
 * type for trend
 */
public enum DirectionType {
    Chaos,
    Down,
    Up,
    ;

    @Override
    public String toString() {
        return super.toString();
    }

    public static DirectionType getOpposite(DirectionType direction){
        if(direction == null) return Chaos;
        switch (direction){
            case Up: return Down;
            case Down: return Up;
            default: return Chaos;
        }
    }

    public static DirectionType getDirectionType(double value1, double value2){
        if(value1 == value2) return Chaos;
        if(value1 < value2) return Up;
        else return Down;
    }

}
