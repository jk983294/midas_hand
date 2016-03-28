package com.victor.midas.calculator.common.model;


import com.victor.midas.calculator.util.MathStockUtil;

public class IndexLine {

    public IndexPoint point1, point2;
    public DirectionType direction = DirectionType.Chaos;
    public boolean isCrossZero;
    public int cnt;

    public IndexLine(IndexPoint point1) {
        this.point1 = point1;
        cnt = 1;
    }

    public IndexLine(IndexPoint point1, IndexPoint point2) {
        this.point1 = point1;
        this.point2 = point2;
        direction = DirectionType.getDirectionType(point1.value, point2.value);
        cnt = 2;
    }

    /**
     * if this point can fit into this line, return true
     * otherwise return false, which means the direction is the opposite
     * @param point
     * @return
     */
    public boolean update(IndexPoint point){
        isCrossZero = false;
        if(point2 == null || direction == DirectionType.Chaos){
            point2 = point;
            direction = DirectionType.getDirectionType(point1.value, point2.value);
            isCrossZero = MathStockUtil.isCrossZeroUp(point1.value, point2.value);
            ++cnt;
            return true;
        } else if(direction.equals(DirectionType.getDirectionType(point2.value, point.value))){
            isCrossZero = MathStockUtil.isCrossZeroUp(point2.value, point.value);
            point2 = point;
            ++cnt;
            return true;
        }
        return false;
    }

    public boolean isLineCrossZero(){
        return point1 != null && point2 != null && MathStockUtil.isCrossZero(point1.value, point2.value);
    }

    public boolean isLineCrossZeroUp(){
        return point1 != null && point2 != null && MathStockUtil.isCrossZeroUp(point1.value, point2.value);
    }

    public boolean isLineCrossZeroDown(){
        return point1 != null && point2 != null && MathStockUtil.isCrossZeroDown(point1.value, point2.value);
    }
}
