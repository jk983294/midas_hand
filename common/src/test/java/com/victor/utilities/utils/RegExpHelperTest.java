package com.victor.utilities.utils;

import com.victor.utilities.algorithm.strings.RegExpUsage;
import com.victor.utilities.model.KeyValue;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * regular expression helper unit test
 */
public class RegExpHelperTest {

    private final static double delta = 1e-6;

    @Test
    public void testCondense(){
        assertEquals(" hello world ", RegExpHelper.condense("\t hello\t \nworld \t\n"));
    }

    @Test
    public void testcontainLogicOperators(){
        assertEquals(false, RegExpHelper.containLogicOperators("\t hello\t \nworld \t\n"));
        assertEquals(false, RegExpHelper.containLogicOperators("and "));
        assertEquals(false, RegExpHelper.containLogicOperators(" "));
        assertEquals(false, RegExpHelper.containLogicOperators(" or"));
        assertEquals(false, RegExpHelper.containLogicOperators("&& "));
        assertEquals(false, RegExpHelper.containLogicOperators(" ||"));
        assertEquals(true, RegExpHelper.containLogicOperators(" || "));
        assertEquals(true, RegExpHelper.containLogicOperators(" || && "));
        assertEquals(true, RegExpHelper.containLogicOperators(" || and "));
    }

    @Test
    public void testSplitByLogicOperators(){
        String str = " and a && b || c and d or e or ";
        List<String> result = RegExpHelper.splitByLogicOperators(str);
        assertEquals(13, result.size());
        String str1 = "x > 3 y < 5";
        List<String> result1 = RegExpHelper.splitByLogicOperators(str1);
        assertEquals(1, result1.size());
    }

    @Test
    public void testIsNumber(){
        assertEquals(false, RegExpHelper.isNumber("\t hello\t \nworld \t\n"));
        assertEquals(false, RegExpHelper.isNumber("and "));
        assertEquals(true, RegExpHelper.isNumber("3.1415"));
        assertEquals(true, RegExpHelper.isNumber("479"));
        assertEquals(true, RegExpHelper.isNumber("-3.12"));
        assertEquals(true, RegExpHelper.isNumber("-567"));
        assertEquals(true, RegExpHelper.isNumber("- 0.2364"));
        assertEquals(true, RegExpHelper.isNumber("+ 0.2364"));
        assertEquals(true, RegExpHelper.isNumber("+4."));
        assertEquals(true, RegExpHelper.isNumber("-1.2364e12"));
        assertEquals(true, RegExpHelper.isNumber("-1.2364e-12"));
        assertEquals(true, RegExpHelper.isNumber("-1,234,345,234.5435"));
        assertEquals(false, RegExpHelper.isNumber("-1,234,5,234.5435"));
        assertEquals(false, RegExpHelper.isNumber("-1.2364e"));
        Double x = Double.valueOf("+.5");
        assertEquals(false, RegExpHelper.isNumber(""));
    }

    @Test
    public void testIsPercentOrNumber(){
        assertEquals(false, RegExpHelper.isPercentOrNumber("\t hello\t \nworld \t\n"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("3.1415"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("479"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("-3.12%"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("-567"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("- 0.2364 %"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("+ 0.2364  %"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("+4."));
        assertEquals(true, RegExpHelper.isPercentOrNumber("-1,234,345,234.5435"));
        assertEquals(false, RegExpHelper.isPercentOrNumber("-1,234,5,234.5435"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("-1.2364e12"));
        assertEquals(true, RegExpHelper.isPercentOrNumber("-1.2364e-12"));
        assertEquals(false, RegExpHelper.isPercentOrNumber(""));
    }

    @Test
    public void testNumberCount(){
        assertEquals(1, RegExpHelper.numberCount("3.1415"));
        assertEquals(5, RegExpHelper.numberCount("479 -3.12 -567  -0.2364  dsfds +0.2364"));
        assertEquals(3, RegExpHelper.numberCount("+4. -1.2364e12 -1.2364e-12"));
    }

    @Test
    public void testNumberPercentCount(){
        assertEquals(1, RegExpHelper.percentCount("3.1415  %"));
        assertEquals(3, RegExpHelper.percentCount("479 -3.12 -567 %  -0.2364%  dsfds +0.2364%"));
        assertEquals(1, RegExpHelper.percentCount("+4. -1.2364e12% -1.2364e-12"));
    }

    @Test
    public void testPercentOrNumberCount(){
        assertEquals(1, RegExpHelper.percentOrNumberCount("3.1415"));
        assertEquals(5, RegExpHelper.numberCount("479 -3.12% -567  -0.2364 %  dsfds +0.2364"));
        assertEquals(3, RegExpHelper.numberCount("+4. -1.2364e12 % -1.2364e-12"));
    }

    @Test
    public void testIsNumberPercent(){
        assertEquals(false, RegExpHelper.isPercent("3.1415"));
        assertEquals(true, RegExpHelper.isPercent("479%"));
        assertEquals(true, RegExpHelper.isPercent("-3.12 %"));
        assertEquals(true, RegExpHelper.isPercent("-567  %"));
        assertEquals(true, RegExpHelper.isPercent("-1.2364e12%"));
        assertEquals(true, RegExpHelper.isPercent("-1.2364e-12%"));
        assertEquals(false, RegExpHelper.isPercent("-1.2364e%"));
    }

    @Test
    public void testExtractNumber(){
        assertEquals("3.1415", RegExpHelper.extractNumberStr("3.1415"));
        assertEquals("479", RegExpHelper.extractNumberStr("479%"));
        assertEquals("-3.12", RegExpHelper.extractNumberStr("-3.12 %"));
        assertEquals("-567", RegExpHelper.extractNumberStr("-567  %"));
        assertEquals("-3.12", RegExpHelper.extractNumberStr("afds -3.12% -567  -0.2364 %  dsfds +0.2364"));
        assertEquals("-1.2364e12", RegExpHelper.extractNumberStr(" -1.2364e12 % -1.2364e-12"));
        assertEquals("-1,234,345,234.5435e2", RegExpHelper.extractNumberStr(" -1,234,345,234.5435e2 "));
    }

    @Test
    public void testExtractPercentOrNumber(){
        assertEquals("3.1415", RegExpHelper.extractPercentOrNumberStr("3.1415"));
        assertEquals("479%", RegExpHelper.extractPercentOrNumberStr("479%"));
        assertEquals("-3.12 %", RegExpHelper.extractPercentOrNumberStr("-3.12 %"));
        assertEquals("-567  %", RegExpHelper.extractPercentOrNumberStr("-567  %"));
        assertEquals("-3.12%", RegExpHelper.extractPercentOrNumberStr("afds -3.12% -567  -0.2364 %  dsfds +0.2364"));
        assertEquals("-1.2364e12 %", RegExpHelper.extractPercentOrNumberStr(" -1.2364e12 % -1.2364e-12"));
    }

    @Test
    public void testGetNumber(){
        assertEquals(3.1415, RegExpHelper.getNumber("3.1415"), delta);
        assertEquals(479 * 0.01, RegExpHelper.getNumber("479%"), delta);
        assertEquals(-3.12 * 0.01, RegExpHelper.getNumber("-3.12 %"), delta);
        assertEquals(-567 * 0.01, RegExpHelper.getNumber("-567  %"), delta);
        assertEquals(-3.12 * 0.01, RegExpHelper.getNumber("afds -3.12% -567  -0.2364 %  dsfds +0.2364"), delta);
        assertEquals(-1.2364e12 * 0.01, RegExpHelper.getNumber(" -1.2364e12 % -1.2364e-12"), delta);
        assertEquals(-1234345234.5435e-10, RegExpHelper.getNumber(" -1,234,345,234.5435e-10 "), delta);
    }

    @Test
    public void testExtractRelationalOperatorStr(){
        assertEquals(">", RegExpHelper.extractRelationalOperatorStr(" > 3.1415"));
        assertEquals(">=", RegExpHelper.extractRelationalOperatorStr(">=479%"));
        assertEquals("<=", RegExpHelper.extractRelationalOperatorStr("<= <-3.12 %"));
        assertEquals(">", RegExpHelper.extractRelationalOperatorStr(">-567  %"));
        assertEquals("≥", RegExpHelper.extractRelationalOperatorStr("≥≥≥afds -3.12% -567  -0.2364 %  dsfds +0.2364"));
        assertEquals("≥", RegExpHelper.extractRelationalOperatorStr(" -1.2364e12 % ≥-1.2364e-12"));
    }

    @Test
    public void testIsVariable(){
        assertEquals(true, RegExpHelper.isVariable("x=1"));
        assertEquals(true, RegExpHelper.isVariable("y = 3.14"));
        assertEquals(true, RegExpHelper.isVariable("k= 5.324"));
        assertEquals(false, RegExpHelper.isVariable("= 3.14"));
        assertEquals(false, RegExpHelper.isVariable("3.14"));
    }

    @Test
    public void testExtractVariable(){
        List<KeyValue<String, Double>> results = RegExpHelper.extractVariable("x=1 y = 3.14 new variable = 4.3some variable = 7.8");
        assertEquals(4, results.size());
        assertEquals("x", results.get(0).getKey());
        assertEquals("y", results.get(1).getKey());
        assertEquals("new variable", results.get(2).getKey());
        assertEquals("some variable", results.get(3).getKey());
        assertEquals(1d, results.get(0).getValue(), delta);
        assertEquals(3.14, results.get(1).getValue(), delta);
        assertEquals(4.3, results.get(2).getValue(), delta);
        assertEquals(7.8, results.get(3).getValue(), delta);
    }

    @Test
    public void testExtractFirstVariableValue(){
        assertEquals(1d, RegExpHelper.extractFirstVariableValue("x=1 y = 3.14 new variable = 4.3some variable = 7.8"), delta);
        assertEquals(3.14, RegExpHelper.extractFirstVariableValue(" y = 3.14 new variable = 4.3some variable = 7.8"), delta);
        assertEquals(4.3, RegExpHelper.extractFirstVariableValue(" new variable = 4.3some variable = 7.8"), delta);
        assertEquals(7.8, RegExpHelper.extractFirstVariableValue("some variable = 7.8"), delta);
    }

    @Test
    public void testIsTime(){
        assertEquals(true, RegExpHelper.containTime("2015-11-09 20:59:05"));
        assertEquals(true, RegExpHelper.containTime("475934 2015-11-09 20:59:05 45943875"));
        assertEquals(true, RegExpHelper.containTime("gdfg 2015-11-09 20:59:05 sdgdsg"));
        assertEquals("2015-11-09 20:59:05", RegExpHelper.extractTimeStr("2015-11-09 20:59:05"));
        assertEquals("2015-11-09 20:59:05", RegExpHelper.extractTimeStr("475934 2015-11-09 20:59:05 45943875"));
        assertEquals("2015-11-09 20:59:05", RegExpHelper.extractTimeStr("gdfg 2015-11-09 20:59:05 sdgdsg"));
    }

    @Test
    public void testContainVariable(){
        assertEquals(true, RegExpHelper.containVariable("x=1"));
        assertEquals(true, RegExpHelper.containVariable("  x=1"));
        assertEquals(true, RegExpHelper.containVariable("s x=1"));
        assertEquals(true, RegExpHelper.containVariable("x =1"));
        assertEquals(true, RegExpHelper.containVariable(" x=1   "));
        assertEquals(true, RegExpHelper.containVariable(" x= -1 "));
    }

    @Test
    public void testCalculator(){
        assertEquals(false, RegExpUsage.calculateWithLogicalOperator("50", "12", "Number"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator("50", "70", "Number"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator("50", "50", "Number"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator("< 50", "12", "Number"));
        assertEquals(false, RegExpUsage.calculateWithLogicalOperator(">= 50", "12", "Number"));
        assertEquals(false, RegExpUsage.calculateWithLogicalOperator("< 50 or > 100", "80", "Number"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator(">= 50 and <= 100", "80", "Number"));
        assertEquals(false, RegExpUsage.calculateWithLogicalOperator(">= 25", "1Q = 13", "Number"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator(">= 25 and <= 30", "1Q = 27", "Number"));

        assertEquals(false, RegExpUsage.calculateWithLogicalOperator("50%", "12%", "Percentage"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator("50%", "70%", "Percentage"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator("50%", "50%", "Percentage"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator("< 50%", "12%", "Percentage"));
        assertEquals(false, RegExpUsage.calculateWithLogicalOperator(">= 50%", "12%", "Percentage"));
        assertEquals(false, RegExpUsage.calculateWithLogicalOperator("< 50% or > 100%", "80%", "Percentage"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator(">= 50% and <= 100%", "80%", "Percentage"));
        assertEquals(true, RegExpUsage.calculateWithLogicalOperator(">= 25% and <= 30%", "1Q = 27%", "Percentage"));
    }
}
