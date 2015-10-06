package com.victor.utilities.math.function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * SectionalFunction unit test
 */
public class SectionalFunctionTest {

    private final static double epsilon = 1e-6;

    @Test
    public void calculateTwoPointsTest() {
        SectionalFunction sectionalFunction = new SectionalFunction(0d, 0d, 1d, 1d);

        assertEquals(0d, sectionalFunction.calculate( -0.5), epsilon);
        assertEquals(1d, sectionalFunction.calculate( 1.5), epsilon);
        assertEquals(0.5d, sectionalFunction.calculate( 0.5), epsilon);
    }

    @Test
    public void calculateThreePointsTest() {
        SectionalFunction sectionalFunction = new SectionalFunction(-1d, -1d, 0d, 0d, 1d, 1d);

        assertEquals(-1d, sectionalFunction.calculate( -1.5), epsilon);
        assertEquals(1d, sectionalFunction.calculate( 1.5), epsilon);
        assertEquals(0.5d, sectionalFunction.calculate( 0.5), epsilon);
        assertEquals(-0.5d, sectionalFunction.calculate( -0.5), epsilon);
    }
}
