package com.victor.utilities.utils;


import com.victor.utilities.model.KeyValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regular expression helper
 */
public class RegExpHelper {

    private static final Logger logger = Logger.getLogger(RegExpHelper.class);

    private static final String numberPatternStr = "[-+]?\\s*(\\d+(,\\d{3})*(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?";
    private static final String percentPatternStr = numberPatternStr + "\\s*%";
    private static final String percentOrNumberPatternStr = numberPatternStr + "(\\s*%)?";
    private static final String unitStr = "%|k|mm|m|bn|bps";
    private static final String numberUnitPatternStr = "((" + numberPatternStr + ")(\\s*("+unitStr+"))?)";

    /**
     * search pattern
     */
    private static final Pattern digitPattern = Pattern.compile("\\d");
    private static final Pattern condensePattern = Pattern.compile("\\s+");
    private static final Pattern numberPattern = Pattern.compile(numberPatternStr);
    private static final Pattern percentPattern = Pattern.compile(percentPatternStr);
    private static final Pattern percentOrNumberPattern = Pattern.compile(percentOrNumberPatternStr);
    private static final Pattern variablePattern = Pattern.compile("(\\w+(\\s*\\w+)*)\\s*=\\s*" + numberUnitPatternStr);
    private static final Pattern arithmeticPattern = Pattern.compile("\\w+\\s*(\\+|-|\\*|/)\\s*" + numberUnitPatternStr + "\\s*(\\w+)?");
    private static final Pattern numberUnitPattern = Pattern.compile(numberUnitPatternStr);
    private static final Pattern footerPattern = Pattern.compile(".*(\\(\\d+\\))$");
    private static final Pattern timePattern = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*");
    private static final Pattern intPattern = Pattern.compile("[-+]?\\d+");
    private static final Pattern doublePattern = Pattern.compile("[-+]?\\d+\\.\\d+");

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

    /**
     * replace several space characters to one space
     */
    public static String condense(String str){
        Matcher matcher = condensePattern.matcher(str);
        return matcher.replaceAll(" ");
    }

    /**
     * split by and or && ||
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
     */
    public static String extractNumberStr(String str){
        return extract(str, numberExtractPattern);
    }

    /**
     * this will extract first number or percent occurrence
     */
    public static String extractPercentOrNumberStr(String str){
        return extract(str, percentOrNumberExtractPattern);
    }

    public static String extractRelationalOperatorStr(String str){
        return extract(str, relationalOperatorPattern);
    }

    public static List<KeyValue<String, Double>> extractVariable(String str){
        Matcher matcher = variablePattern.matcher(str);
        List<KeyValue<String, Double>> results = new ArrayList<>();
        while(matcher.find()){
            KeyValue<String, Double> result = new KeyValue<>(matcher.group(1), Double.valueOf(removeFormatForNumberStr(matcher.group(3))));
            results.add(result);
        }
        return results;
    }

    public static List<String> extractArithmeticPattern(String str){
        Matcher matcher = arithmeticPattern.matcher(str);
        List<String> results = new ArrayList<>();
        while(matcher.find()){
            results.add(matcher.group(1));  // operator
            results.add(matcher.group(2));  // number
        }
        return results;
    }

    public static Double extractFirstVariableValue(String str){
        Matcher matcher = variablePattern.matcher(str);
        if(matcher.find()){
            String numberStr = matcher.group(3);
            return getNumber(numberStr);
        }
        return null;
    }

    public static String extractFooter(String str){
        return extract(str, footerPattern);
    }

    public static String extractTimeStr(String str){
        return extract(str, timePattern);
    }

    /**
     * deal with percent character "%" conversion
     */
    public static Double getNumber(String number){
        if(StringUtils.isEmpty(number) || !containDigit(number)){
            return null;
        }
        if(containVariable(number)){
            return extractFirstVariableValue(number);
        }
        String firstNumberStr = null, numberStr = null, unitStr = null;
        if(contains(number, numberUnitPattern)){
            firstNumberStr = removeFormatForNumberStr(extract(number, numberUnitPattern));
            Matcher matcher = numberUnitPattern.matcher(firstNumberStr);
            while(matcher.find()){
                numberStr = matcher.group(2);  // number
                if(matcher.group(8) != null){
                    unitStr = matcher.group(8);  // unit
                }
            }
        }

        if(StringUtils.isNotEmpty(firstNumberStr)){
            Double result = Double.valueOf(numberStr);
            if(StringUtils.isNotEmpty(unitStr)){
                switch (unitStr){
                    case "bps" : result *= 0.0001; break;
                    case "%" : result *= 0.01; break;
                    case "k" : result *= 1e3; break;
                    case "m" :
                    case "mm" : result *= 1e6; break;
                    case "bn" : result *= 1e9; break;
                }
            }
            return result;
        }
        return null;
    }

    /**
     * contain and or && ||
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

    public static boolean containTime(String str){
        return containDigit(str) && contains(str, timePattern);
    }

    public static boolean containDigit(String str){
        return contains(str, digitPattern);
    }

    public static boolean containArithmeticPattern(String str){
        return containDigit(str) && contains(str, arithmeticPattern);
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

    public static boolean isInt(String str){
        return str != null && intPattern.matcher(str).matches();
    }

    public static boolean isDouble(String str){
        return str != null && doublePattern.matcher(str).matches();
    }

    public static boolean isInts(List<String> strs){
        for(String str : strs){
            if(!isInt(str)){
                return false;
            }
        }
        return true;
    }

    public static boolean isDoubles(List<String> strs){
        for(String str : strs){
            if(!isDouble(str)){
                return false;
            }
        }
        return true;
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

    public static boolean isFooter(String str){
        return str != null && footerPattern.matcher(str).matches();
    }

    /**
     * util functions
     */

    public static boolean contains(String str, Pattern pattern){
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
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
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isMatch(String str, Pattern pattern){
        return str != null && pattern.matcher(str).matches();
    }

    /**
     * remove space and ,
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