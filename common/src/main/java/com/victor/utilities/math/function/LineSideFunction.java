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
public class LineSideFunction {

    private Point2D p1, p2;

    private double k, b;

    private List<Point2D> points;

    /**
     * other points are in left of that line
     */
    private boolean isAllPointsLeft = false;

    public LineSideFunction(List<Point2D> points, boolean isAllPointsLeft) {
        update(points, isAllPointsLeft);
    }

    public void update(List<Point2D> points, boolean isAllPointsLeft){
        this.points = points;
        this.isAllPointsLeft = isAllPointsLeft;
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
                success = true;
                for (int k = 0; k < len; k++) {
                    if(k != i && k != j){
                        p3 = points.get(k);
                        if(isAllPointsLeft && isLeft(p1, p2, p3)){
                        } else if(!isAllPointsLeft && isRight(p1, p2, p3)){
                        } else if(isInLine(p1, p2, p3)){
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
     * use cross product to judge which side c is in
     */
    public boolean isLeft(Point2D a, Point2D b, Point2D c){
        return ((b.getX() - a.getX())*(c.getY() - a.getY()) - (b.getY() - a.getY())*(c.getX() - a.getX())) > 0d;
    }

    public boolean isRight(Point2D a, Point2D b, Point2D c){
        return ((b.getX() - a.getX())*(c.getY() - a.getY()) - (b.getY() - a.getY())*(c.getX() - a.getX())) < 0d;
    }

    public boolean isInLine(Point2D a, Point2D b, Point2D c){
        return MathHelper.isEqual((b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX()), 0d);
    }

    public boolean isInLine(Point2D c){
        return MathHelper.isEqual((p2.getX() - p1.getX())*(c.getY() - p1.getY()) - (p2.getY() - p1.getY())*(c.getX() - p1.getX()), 0d);
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

    public boolean isAllPointsLeft() {
        return isAllPointsLeft;
    }

    public void setAllPointsLeft(boolean isAllPointsLeft) {
        this.isAllPointsLeft = isAllPointsLeft;
    }

    @Override
    public String toString() {
        return "LineSideFunction{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", k=" + k +
                ", b=" + b +
                ", points=" + points +
                ", isAllPointsLeft=" + isAllPointsLeft +
                '}';
    }
}
