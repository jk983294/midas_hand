package com.victor.utilities.math.function;

import com.victor.utilities.math.function.model.Point2D;
import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * SectionalFunction unit test
 */
public class LineUpDownSideFunctionTest {

    private final static double epsilon = 1e-6;



    @Test
    public void leftTest() {
        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(0d, 0d));
        points.add(new Point2D(1d, 1d));
        points.add(new Point2D(2d, 3d));
        points.add(new Point2D(3d, 4d));
        points.add(new Point2D(5d, 6d));
        points.add(new Point2D(6d, 7d));
        points.add(new Point2D(7d, 7d));
        LineUpDownSideFunction function = new LineUpDownSideFunction(points, false);
        VisualAssist.print(function.calculate(1));
        VisualAssist.print(function.calculate(2));
        VisualAssist.print(function);

//        assertEquals(0d, sectionalFunction.calculate( -0.5), epsilon);
//        assertEquals(1d, sectionalFunction.calculate( 1.5), epsilon);
//        assertEquals(0.5d, sectionalFunction.calculate( 0.5), epsilon);
    }

    @Test
    public void rightTest() {
        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(0d, 0d));
        points.add(new Point2D(1d, 1d));
        points.add(new Point2D(2d, 3d));
        points.add(new Point2D(3d, 4d));
        points.add(new Point2D(5d, 6d));
        points.add(new Point2D(6d, 7d));
        points.add(new Point2D(7d, 7d));
        LineUpDownSideFunction function = new LineUpDownSideFunction(points, true);
        VisualAssist.print(function.calculate(1));
        VisualAssist.print(function.calculate(2));
        VisualAssist.print(function.isInLine(new Point2D(2d, 2d)));
        VisualAssist.print(function);
    }

    @Test
    public void minusRightTest() {
        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(0d, 0d));
        points.add(new Point2D(-1d, 1d));
        points.add(new Point2D(-2d, 3d));
        points.add(new Point2D(-3d, 4d));
        points.add(new Point2D(-5d, 6d));
        points.add(new Point2D(-6d, 7d));
        points.add(new Point2D(-7d, 7d));
        LineUpDownSideFunction function = new LineUpDownSideFunction(points, true);
        VisualAssist.print(function.calculate(1));
        VisualAssist.print(function.calculate(2));
        VisualAssist.print(function.isInLine(new Point2D(-2d, 2d)));
        VisualAssist.print(function);
    }
}
