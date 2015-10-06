package com.victor.utilities.math.function;

import com.victor.utilities.math.function.model.Point2D;

/**
 * y = kx + b
 * y - kx - b = 0
 * f(x) = y - kx - b
 */
public class LineFunction {

    private Point2D p1, p2;

    private double k, b;

    public LineFunction(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
        if(p1.getX() != p2.getX()){
            k = ( p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
            b = p1.getY() - k * p1.getX();
        }
    }

    public double calculate(double x){
        return k * x + b;
    }

    public double calculate(double x, double y){
        return y - k * x - b;
    }

    public Point2D getP1() {
        return p1;
    }

    public void setP1(Point2D p1) {
        this.p1 = p1;
    }

    public Point2D getP2() {
        return p2;
    }

    public void setP2(Point2D p2) {
        this.p2 = p2;
    }
}
