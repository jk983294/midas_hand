package com.victor.utilities.math.function;

import com.victor.utilities.math.function.model.Point2D;
import com.victor.utilities.utils.MathHelper;

import java.util.List;

/**
 * given several points, choose two points let other points are in one side
 * y = kx + b
 * y - kx - b = 0
 * f(x) = y - kx - b
 */
public class LineUpDownSideFunction {

    private Point2D p1, p2;

    private double k, b;

    private List<Point2D> points;

    /**
     * other points are in left of that line
     */
    private boolean isAllPointsUp = false;

    public LineUpDownSideFunction(boolean isAllPointsUp) {
        this.isAllPointsUp = isAllPointsUp;
    }

    public LineUpDownSideFunction(List<Point2D> points, boolean isAllPointsUp) {
        update(points, isAllPointsUp);
    }

    public void update(List<Point2D> points, boolean isAllPointsUp){
        this.points = points;
        this.isAllPointsUp = isAllPointsUp;
        update();
    }

    public void update(List<Point2D> points){
        this.points = points;
        update();
    }

    public void update(){
        if(points == null || points.size() < 2) return;

        int len = points.size();
        Point2D p3 = null;
        boolean success = true;
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 1; j < len; j++) {
                p1 = points.get(i);
                p2 = points.get(j);
                update(p1, p2);
                success = true;
                for (int k = 0; k < len; k++) {
                    if(k != i && k != j){
                        p3 = points.get(k);
                        if(isInLine(p3)){
                        } else if(!isAllPointsUp && isDown(p3)){
                        } else if(isAllPointsUp && isUp(p3)){
                        } else {
                            success = false;
                            break;
                        }
                    }
                }
                if(success) break;
            }
            if(success) break;
        }
        if(success){
            update(p1, p2);
        }
    }

    public void update(Point2D p1, Point2D p2){
        this.p1 = p1;
        this.p2 = p2;
        if(p1.getX() != p2.getX()){
            k = ( p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
            b = p1.getY() - k * p1.getX();
        }
    }

    /**
     * calculate y = kx + b
     */
    public double calculate(double x){
        return k * x + b;
    }

    /**
     * calculate f(x) = y - kx - b
     */
    public double calculate(double x, double y){
        return y - k * x - b;
    }

    /**
     * use f(x) sign decide up or down
     */
    public boolean isUp(Point2D c){
        return calculate(c.getX(), c.getY()) > 0d;
    }

    public boolean isDown( Point2D c){
        return calculate(c.getX(), c.getY()) < 0d;
    }

    public boolean isInLine(Point2D c){
        return MathHelper.isEqual(calculate(c.getX(), c.getY()), 0d);
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

    public List<Point2D> getPoints() {
        return points;
    }

    public void setPoints(List<Point2D> points) {
        this.points = points;
    }

    public boolean isAllPointsUp() {
        return isAllPointsUp;
    }

    public void setAllPointsUp(boolean isAllPointsUp) {
        this.isAllPointsUp = isAllPointsUp;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "LineSideFunction{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", k=" + k +
                ", b=" + b +
                ", points=" + points +
                ", isAllPointsUp=" + isAllPointsUp +
                '}';
    }
}
