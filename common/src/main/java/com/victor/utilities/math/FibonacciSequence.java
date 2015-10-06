package com.victor.utilities.math;

public class FibonacciSequence {

    public static int[] sequence;
    public final static int count = 20;
    static {
        sequence = fibonacciSequences(count);
    }

    public static final int[] fibonacciSequences(int n) {
        int[] array = new int[n + 1];
        int counter = 0;
        while (counter <= n) {
            int r = 0;
            if (counter > 1) {
                r = array[counter - 1] + array[counter - 2];
            } else if (counter == 1) {
                r = 1;
            }
            array[counter] = r;
            counter++;
        }

        return array;
    }

    public static final int queryFibonacciNumber(int n){
        if(n < count) return sequence[n];
        else return fibonacciSequenceUsingBinetsFormula(n);
    }

    private static final double INVERSE_SQUARE_ROOT_OF_5 = 1 / Math.sqrt(5);
    private static final double PHI = (1 + Math.sqrt(5)) / 2; // Golden ratio

    public static final int fibonacciSequenceUsingLoop(int n) {
        int[] array = new int[n + 1];
        int counter = 0;
        while (counter <= n) {
            int r = 0;
            if (counter > 1) {
                r = array[counter - 1] + array[counter - 2];
            } else if (counter == 1) {
                r = 1;
            }
            array[counter] = r;
            counter++;
        }

        return array[n];
    }

    public static final int fibonacciSequenceUsingMatrixMultiplication(int n) {
        // m = [ 1 , 1 ]
        // [ 1 , 0 ]
        int[][] matrix = new int[2][2];
        matrix[0][0] = 1;
        matrix[0][1] = 1;
        matrix[1][0] = 1;
        matrix[1][1] = 0;

        int[][] temp = new int[2][2];
        temp[0][0] = 1;
        temp[0][1] = 1;
        temp[1][0] = 1;
        temp[1][1] = 0;

        int counter = n;
        while (counter > 0) {
            temp = multiplyMatrices(matrix, temp);
            // Subtract an additional 1 the first time in the loop because the
            // first multiplication is
            // actually n -= 2 since it multiplying two matrices
            counter -= (counter == n) ? 2 : 1;
        }
        return temp[0][1];
    }

    private static final int[][] multiplyMatrices(int[][] A, int[][] B) {
        int a = A[0][0];
        int b = A[0][1];
        int c = A[1][0];
        int d = A[1][1];

        int e = B[0][0];
        int f = B[0][1];
        int g = B[1][0];
        int h = B[1][1];

        B[0][0] = a * e + b * g;
        B[0][1] = a * f + b * h;
        B[1][0] = c * e + d * g;
        B[1][1] = c * f + d * h;

        return B;
    }

    public static final int fibonacciSequenceUsingBinetsFormula(int n) {
        return (int) Math.floor(Math.pow(PHI, n) * INVERSE_SQUARE_ROOT_OF_5 + 0.5);
    }
}
