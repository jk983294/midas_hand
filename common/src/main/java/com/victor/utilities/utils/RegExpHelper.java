package com.victor.utilities.utils;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regular expression helper
 */
public class RegExpHelper {

    private static final Logger logger = LoggerFactory.getLogger(RegExpHelper.class);

    private static final String numberPatternStr = "[-+]?\\s*(\\d+(,\\d{3})*(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?";
    private static final String percentPatternStr = numberPatternStr + "\\s*%";
    private static final String percentOrNumberPatternStr = numberPatternStr + "(\\s*%)?";

    /**
     * search pattern
     */
    private static final Pattern digitPattern = Pattern.compile("\\d");
    private static final Pattern condensePattern = Pattern.compile("\\s+");
    private static final Pattern numberPattern = Pattern.compile(numberPatternStr);
    private static final Pattern percentPattern = Pattern.compile(percentPatternStr);
    private static final Pattern percentOrNumberPattern = Pattern.compile(percentOrNumberPatternStr);
    private static final Pattern variablePattern = Pattern.compile("(\\w+(\\s*\\w+)*)\\s*=\\s*(" + percentOrNumberPatternStr + ")");
    private static final Pattern timePattern = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*");

    /**
     * operator pattern
     */
    private static final Pattern relationalOperatorPattern = Pattern.compile("(>=|<=|>|<|≥|≤)");
    private static final Pattern logicOperatorPattern = Pattern.compile(" (and|or|&&|\\|\\|) ");

    /**
     * extract pattern
     */
    private static final Pattern numberExtractPattern = Pattern.compile("(" + numberPatternStr + ")");
    private static final Pattern percentOrNumberExtractPattern = Pattern.compile("(" + numberPatternStr + "(\\s*%)?" + ")");
    private static final Pattern currencyNumberUnitExtractPattern = Pattern.compile("(" + "\\$.*(" + numberPatternStr + ").*(k|m)" + ")");

    /**
     * replace several space characters to one space
     * @param str
     * @return
     */
    public static String condense(String str){
        Matcher matcher = condensePattern.matcher(str);
        return matcher.replaceAll(" ");
    }

    /**
     * split by and or && ||
     * @param str
     * @return
     */
    public static List<String> splitByLogicOperators(String str){
        Matcher matcher = logicOperatorPattern.matcher(str);
        List<String> result = new ArrayList<>();
        int start = 0;
        while (matcher.find()) {
            result.add(str.substring(start, matcher.start()).trim());
            result.add(str.substring(matcher.start(), matcher.end()).trim());
            start = matcher.end();
        }
        result.add(str.substring(start, str.length()).trim());
        return result;
    }

    /**
     * this will extract first number occurrence
     * @param str
     * @return
     */
    public static String extractNumberStr(String str){
        return extract(str, numberExtractPattern);
    }

    public static String extractTimeStr(String str){
        return extract(str, timePattern);
    }

    /**
     * this will extract first number or percent occurrence
     * @param str
     * @return
     */
    public static String extractPercentOrNumberStr(String str){
        return extract(str, percentOrNumberExtractPattern);
    }

    public static String extractRelationalOperatorStr(String str){
        return extract(str, relationalOperatorPattern);
    }

    public static String extractCurrencyNumberUnitStr(String str){
        return extract(str, currencyNumberUnitExtractPattern);
    }

    public static List<DefaultKeyValue<String, Double>> extractVariable(String str){
        Matcher matcher = variablePattern.matcher(str);
        List<DefaultKeyValue<String, Double>> results = new ArrayList<>();
        while(matcher.find()){
            DefaultKeyValue<String, Double> result = new DefaultKeyValue<>(matcher.group(1), Double.valueOf(removeFormatForNumberStr(matcher.group(3))));
            results.add(result);
        }
        return results;
    }

    public static Double extractFirstVariableValue(String str){
        Matcher matcher = variablePattern.matcher(str);
        while(matcher.find()){
            String numberStr = matcher.group(3);
            if(isPercent(numberStr)){
                return Double.valueOf(removeFormatForNumberStr(extractNumberStr(numberStr))) * 0.01;
            } else if(isNumber(numberStr)){
                return Double.valueOf(removeFormatForNumberStr(numberStr));
            }
            return null;
        }
        return null;
    }

    /**
     * deal with percent character "%" conversion
     * @param number
     * @return
     */
    public static Double getNumber(String number){
        if(containVariable(number)){
            return extractFirstVariableValue(number);
        } else {
            String firstNumber = removeFormatForNumberStr(extractPercentOrNumberStr(number));
            if(isPercent(firstNumber)){
                return Double.valueOf(extractNumberStr(firstNumber)) * 0.01;
            } else if(isNumber(firstNumber)){
                return Double.valueOf(firstNumber);
            } else {
                return null;
            }
        }
    }

    /**
     * contain and or && ||
     * @param str
     * @return
     */
    public static boolean containLogicOperators(String str){
        return contains(str, logicOperatorPattern);
    }

    public static boolean containNumber(String str){
        return contains(str, numberPattern);
    }

    public static boolean containPercent(String str){
        return contains(str, percentPattern);
    }

    public static boolean containPercentOrNumber(String str){
        return contains(str, percentOrNumberPattern);
    }

    public static boolean containRelationalOperator(String str){
        return contains(str, relationalOperatorPattern);
    }

    public static boolean containVariable(String str){
        return str.contains("=") && contains(str, variablePattern);
    }

    public static boolean containDigit(String str){
        return contains(str, digitPattern);
    }

    public static boolean containTime(String str){
        return containDigit(str) && contains(str, timePattern);
    }

    /**
     * get occurrence count for reg exp pattern
     */
    public static int numberCount(String str){
        return containDigit(str) ? count(str, numberPattern) : 0;
    }

    public static int percentCount(String str){
        return containDigit(str) ? count(str, percentPattern) : 0;
    }

    public static int percentOrNumberCount(String str){
        return containDigit(str) ? count(str, percentOrNumberPattern) : 0;
    }

    public static int numberRelationalOperatorCount(String str){
        return count(str, relationalOperatorPattern);
    }

    /**
     * check if it matches reg exp pattern
     */
    public static boolean isNumber(String str){
        return str != null && numberPattern.matcher(str).matches();
    }

    public static boolean isPercent(String str){
        return str != null && percentPattern.matcher(str).matches();
    }

    public static boolean isPercentOrNumber(String str){
        return str != null && percentOrNumberPattern.matcher(str).matches();
    }

    public static boolean isVariable(String str){
        return str != null && variablePattern.matcher(str).matches();
    }

    /**
     * util functions
     */

    public static boolean contains(String str, Pattern pattern){
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            return true;
        }
        return false;
    }

    public static int count(String str, Pattern pattern){
        Matcher matcher = pattern.matcher(str);
        int cnt = 0;
        while (matcher.find()){
            ++cnt;
        }
        return cnt;
    }

    public static String extract(String str, Pattern pattern){
        if(str == null) return null;
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    /**
     * remove space and ,
     * @param str
     * @return
     */
    private static String removeFormatForNumberStr(String str){
        String result = str;
        if(result != null){
            if(result.contains(",")){
                result = result.replace(",", "");
            }
            if(result.contains(" ")){
                result = result.replace(" ", "");
            }
        }
        return result;
    }

}
