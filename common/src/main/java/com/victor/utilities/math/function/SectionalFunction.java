package com.victor.utilities.math.function;

/**
 * SectionalFunction
 * x1 <= x2 <= x3
 */
public class SectionalFunction {

    private double x1, y1;

    private double x2, y2;

    private double x3, y3;

    private double k, b;

    private double k1, b1;

    private int pointCnt;

    public SectionalFunction(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        if(x1 != x2){
            k = ( y2 - y1) / (x2 - x1);
            b = y1 - k * x1;
        }
        pointCnt = 2;
    }

    public SectionalFunction(double x1, double y1, double x2, double y2, double x3, double y3) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;

        if(x1 != x2){
            k = ( y2 - y1) / (x2 - x1);
            b = y1 - k * x1;
        }
        if(x2 != x3){
            k1 = ( y3 - y2) / (x3 - x2);
            b1 = y2 - k1 * x2;
        }
        pointCnt = 3;
    }

    public double calculate(double x){
        if(pointCnt == 2) {
            return calculateTwoPoints(x);
        } else {
            return calculateThreePoints(x);
        }
    }

    private double calculateTwoPoints(double x){
        if(x <= x1){
            return y1;
        } else if(x >= x2){
            return y2;
        } else if(x1 == x2){
            return y2;
        } else {
            return k * x + b;
        }
    }

    private double calculateThreePoints(double x){
        if(x <= x1){
            return y1;
        } else if(x >= x3){
            return y3;
        } else if(x <= x2){
            if(x1 == x2){
                return y2;
            } else {
                return k * x + b;
            }
        } else {
            if(x2 == x3){
                return y3;
            } else {
                return k1 * x + b1;
            }
        }
    }


}
