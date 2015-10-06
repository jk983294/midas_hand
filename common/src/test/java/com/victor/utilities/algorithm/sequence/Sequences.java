package com.victor.utilities.algorithm.sequence;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.victor.utilities.algorithm.dynamicprogramming.LongestCommonSubsequence;
import com.victor.utilities.math.FibonacciSequence;

public class Sequences {

    @Test
    public void testFibonacci() {
        // COMPUTE FIBONACCI SEQUENCE
        int element = 25;
        int check = 75025;
        int result = FibonacciSequence.fibonacciSequenceUsingLoop(element);
        assertTrue("Fibonacci Sequence Using Loop error. result="+result+" check="+check, (result==check));

        result = FibonacciSequence.fibonacciSequenceUsingMatrixMultiplication(element);
        assertTrue("Fibonacci Sequence Using Matrix error. result="+result+" check="+check, (result==check));

        result = FibonacciSequence.fibonacciSequenceUsingBinetsFormula(element);
        assertTrue("Fibonacci Sequence Using Binet's formula error. result="+result+" check="+check, (result==check));
    }

    @Test
    public void testLongestCommonSubSequences() {
        // LONGEST COMMON SUBSEQUENCE
        int resultLength = 2;
        Set<String> resultSequence = new HashSet<String>();
        resultSequence.add("AC");
        resultSequence.add("GC");
        resultSequence.add("GA");
        char[] seq1 = new char[] { 'G', 'A', 'C' };
        char[] seq2 = new char[] { 'A', 'G', 'C', 'A', 'T' };
        LongestCommonSubsequence.MatrixPair pair = LongestCommonSubsequence.getLCS(seq1, seq2);
        assertTrue("Longest common subsequence error. "+
                   "resultLength="+resultLength+" seqLength="+pair.getLongestSequenceLength()+" "+
                   "resultSequence="+resultSequence+" sequence="+pair.getLongestSequences(), 
                   (resultLength==pair.getLongestSequenceLength() && 
                    resultSequence.equals(pair.getLongestSequences())));

        resultLength = 3;
        resultSequence.clear();
        resultSequence.add("GAX");
        resultSequence.add("ACT");
        resultSequence.add("GCT");
        resultSequence.add("GAT");
        resultSequence.add("ACX");
        resultSequence.add("GCX");
        seq1 = new char[] { 'G', 'A', 'C', 'V', 'X', 'T' };
        seq2 = new char[] { 'A', 'G', 'C', 'A', 'T', 'X' };
        pair = LongestCommonSubsequence.getLCS(seq1, seq2);
        assertTrue("Longest common subsequence error. "+
                   "resultLength="+resultLength+" seqLength="+pair.getLongestSequenceLength()+" "+
                   "resultSequence="+resultSequence+" sequence="+pair.getLongestSequences(), 
                   (resultLength==pair.getLongestSequenceLength() && 
                    resultSequence.equals(pair.getLongestSequences())));
    }
}
