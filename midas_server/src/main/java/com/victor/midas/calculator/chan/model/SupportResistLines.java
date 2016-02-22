package com.victor.midas.calculator.chan.model;

import com.victor.midas.calculator.common.model.DirectionType;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.utilities.math.function.LineUpDownSideFunction;
import com.victor.utilities.math.function.SectionalFunction;
import com.victor.utilities.math.function.model.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * maintain support and resist line
 */
public class SupportResistLines {

    private LineUpDownSideFunction support = new LineUpDownSideFunction(true);

    private LineUpDownSideFunction resist = new LineUpDownSideFunction(false);

    private List<Point2D> supportPoints = new ArrayList<>();

    private List<Point2D> resistPoints = new ArrayList<>();

    public SupportResistLines() {
    }


    public void updateStroke(ChanStroke stroke){
        if(DirectionType.Down.equals(stroke.getType())){
            Point2D point = supportPoints.get(supportPoints.size() - 1);
            point.setX(stroke.getLowIndex());
            point.setY(stroke.getLow());
            supportPoints.remove(supportPoints.size() - 1);
            updateSupport(point);
        } else {    // up
            Point2D point = resistPoints.get(resistPoints.size() - 1);
            point.setX(stroke.getHighIndex());
            point.setY(stroke.getHigh());
            resistPoints.remove(resistPoints.size() - 1);
            updateResist(point);
        }
    }

    public void addStroke(ChanStroke stroke){
        //supportPoints.add(new Point2D(stroke.getLowIndex(), stroke.getLow()));
        //resistPoints.add(new Point2D(stroke.getHighIndex(), stroke.getHigh()));
        updateSupport(new Point2D(stroke.getLowIndex(), stroke.getLow()));
        updateResist(new Point2D(stroke.getHighIndex(), stroke.getHigh()));
    }

    private void updateSupport(Point2D point){
        //Point2D point = supportPoints.get(supportPoints.size() - 1);
        if(supportPoints.size() > 1){
            double changPct = 0d;
            while (supportPoints.size() > 1){
                support.update(supportPoints);
                changPct = MathStockUtil.calculateChangePct(point.getY(), support.calculate(point.getX()));
                if(changPct > 0.1 || changPct < -0.2){
                    supportPoints.remove(0);
                } else {
                    break;
                }
            }
            if(supportPoints.size() == 1 && (changPct > 0.2 || point.getY() < supportPoints.get(0).getY())){
                supportPoints.clear();
            }
        }
        supportPoints.add(point);
        support.update(supportPoints);
    }

    private void updateResist(Point2D point){
        //Point2D point = resistPoints.get(resistPoints.size() - 1);
        if(resistPoints.size() > 1){
            double changPct = 0d;
            while (resistPoints.size() > 1){
                resist.update(resistPoints);
                changPct = MathStockUtil.calculateChangePct(resist.calculate(point.getX()), point.getY());
                if(changPct > 0.1 || changPct < -0.2){
                    resistPoints.remove(0);
                } else {
                    break;
                }
            }
            if(resistPoints.size() == 1 && (changPct > 0.2 || point.getY() > resistPoints.get(0).getY())){
                resistPoints.clear();
            }
        }
        resistPoints.add(point);
        resist.update(resistPoints);
    }

    private static final SectionalFunction positionMaxFunc = new SectionalFunction(0.0, 0d, 0.4d, 1d);
    private static final SectionalFunction positionMinFunc = new SectionalFunction(-0.02, 0.0, 0.0, 1d, 0.4d, 0d);
    private static final SectionalFunction positionRangeFunc = new SectionalFunction(0.00, 0d, 0.5d, 1d);
    private static final SectionalFunction xielvFunc = new SectionalFunction(0.00, 1d, 2d, 0d);
    public double getScore(double index, double closePrice){
        double score = 0d, supportPrice = 0d, resistPrice = 0d;
        Point2D point = null;
        if(supportPoints.size() == 0){
            score += 0d;
        } else if(supportPoints.size() == 1){
            point = supportPoints.get(0);
            supportPrice = point.getY();
            score += positionMinFunc.calculate(MathStockUtil.calculateChangePct(supportPrice, closePrice)) * Math.log10(supportPoints.size() + 10);
        } else {
            supportPrice = support.calculate(index);
            score += positionMinFunc.calculate(MathStockUtil.calculateChangePct(supportPrice, closePrice));// * Math.log10(supportPoints.size() + 10);
            score += xielvFunc.calculate(Math.abs(support.getK()));
        }

        if(resistPoints.size() == 0){
            score += 0d;
        } else if(resistPoints.size() == 1){
            point = resistPoints.get(0);
            resistPrice = point.getY();
            score += positionMaxFunc.calculate(MathStockUtil.calculateChangePct(closePrice, resistPrice)) * Math.log10(resistPoints.size() + 10);
        } else {
            resistPrice = resist.calculate(index);
            score += positionMaxFunc.calculate(MathStockUtil.calculateChangePct(closePrice, resistPrice));// * Math.log10(resistPoints.size() + 10);
            score += xielvFunc.calculate(Math.abs(resist.getK()));
        }

        if(supportPoints.size() > 1 && resistPoints.size() > 1){
            score += positionRangeFunc.calculate(MathStockUtil.calculateChangePct(supportPrice, resistPrice));
        }
        return score;
    }
}
