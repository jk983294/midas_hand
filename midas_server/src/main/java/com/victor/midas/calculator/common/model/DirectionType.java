package com.victor.midas.calculator.common.model;

/**
 * type for trend
 */
public enum DirectionType {

    Down,
    Up,
    Chaos;

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

}
