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
    private static final Pattern currencyNumberUnitHintPattern = Pattern.compile("\\$.*(k|m)");
    private static final Pattern currencyNumberUnitPattern = Pattern.compile("\\$.*(" + numberPatternStr + ").*(k|m)");
    private static final Pattern variablePattern = Pattern.compile("(\\w+(\\s*\\w+)*)\\s*=\\s*(" + percentOrNumberPatternStr + ")");
    private static final Pattern textPattern = Pattern.compile(".*text.*\\(.*red.*amber.*green\\).*");

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

    public static int currencyNumberUnitCount(String str){
        return containDigit(str) ? count(str, currencyNumberUnitPattern) : 0;
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

    public static boolean isCurrencyNumberUnitHint(String str){
        return str != null && currencyNumberUnitHintPattern.matcher(str).matches();
    }

    public static boolean isCurrencyNumberUnit(String str){
        return str != null && currencyNumberUnitPattern.matcher(str).matches();
    }

    public static boolean isVariable(String str){
        return str != null && variablePattern.matcher(str).matches();
    }

    public static boolean isText(String str){
        return str != null && textPattern.matcher(str).matches();
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

    private static int count(String str, Pattern pattern){
        Matcher matcher = pattern.matcher(str);
        int cnt = 0;
        while (matcher.find()){
            ++cnt;
        }
        return cnt;
    }

    private static String extract(String str, Pattern pattern){
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

    // ------------------------------------ Application ----------------------------------------------------

    /**
     * split threshold by Logical Operator ( and or ), get the final result
     * @param threshold
     * @param level
     * @param hint
     * @return
     */
    public static Boolean calculateWithLogicalOperator(String threshold, String level, String hint){
        if(StringUtils.isEmpty(threshold) || StringUtils.isEmpty(level) || StringUtils.isEmpty(hint)
                || "N/A".equalsIgnoreCase(threshold)){
            return null;
        } else {
            threshold = RegExpHelper.condense(threshold).trim().toLowerCase();
            level = RegExpHelper.condense(level).trim().toLowerCase();
            hint = RegExpHelper.condense(hint).trim().toLowerCase();
            List<String> thresholds = RegExpHelper.splitByLogicOperators(threshold);
            if(thresholds.size() > 0 && thresholds.size() % 2 == 1){
                Boolean leftResult = calculateWithoutLogicalOperator(thresholds.get(0), level, hint), rightResult;
                for(int i = 1; i < thresholds.size(); i += 2){
                    String logicalOperator = thresholds.get(i);
                    rightResult = calculateWithoutLogicalOperator(thresholds.get(i + 1), level, hint);
                    leftResult = merge(leftResult, rightResult, logicalOperator);
                }
                return leftResult;
            }
            return null;
        }
    }

    /**
     * merge result by Logical Operator ( and or )
     * @param left
     * @param right
     * @param logicalOperator
     * @return
     */
    private static Boolean merge(Boolean left, Boolean right, String logicalOperator){
        if(left == null){
            return right;
        } else if(right == null){
            return left;
        } else {
            switch(logicalOperator){
                case "and" :
                case "&&" : return left && right;
                case "or" :
                case "||" : return left || right;
                default: throw new RuntimeException("unknown logical operator " + logicalOperator);
            }
        }
    }

    /**
     * deal with threshold and level are both number or percentage
     * @param threshold
     * @param level
     * @return
     */
    private static Boolean calculateNumberCase(String threshold, String level){
        Double levelValue = RegExpHelper.getNumber(level), thresholdValue;
        if(levelValue == null){
            return null;
        }
        int relationalOperatorCount = RegExpHelper.numberRelationalOperatorCount(threshold);
        int percentOrNumberCount = RegExpHelper.percentOrNumberCount(threshold);
        if(percentOrNumberCount == 1){
            thresholdValue = RegExpHelper.getNumber(threshold);
            if(relationalOperatorCount == 0){
                return compare(null, thresholdValue, levelValue);
            } else if(relationalOperatorCount == 1){
                String operator = RegExpHelper.extractRelationalOperatorStr(threshold);
                return compare(operator, thresholdValue, levelValue);
            }
        }
        return null;
    }

    /**
     * deal with currency number unit case ( $$MM $$K )
     * @param threshold
     * @param level
     * @param hint
     * @return
     */
    private static Boolean calculateCurrencyNumberCase(String threshold, String level, String hint){
        if(!validateCurrencyAndUnit(level, hint)){
            return null;
        }
        Double levelValue = RegExpHelper.getNumber(level), thresholdValue;
        if(levelValue == null){
            return null;
        }
        int relationalOperatorCount = RegExpHelper.numberRelationalOperatorCount(threshold);
        int currencyNumberUnitCount = RegExpHelper.currencyNumberUnitCount(threshold);
        if(currencyNumberUnitCount == 1){
            String a = RegExpHelper.extractCurrencyNumberUnitStr(threshold);
            if(!validateCurrencyAndUnit(a, hint)){
                return null;
            }
            thresholdValue = RegExpHelper.getNumber(threshold);
            if(relationalOperatorCount == 0){
                return compare(null, thresholdValue, levelValue);
            } else if(relationalOperatorCount == 1){
                String operator = RegExpHelper.extractRelationalOperatorStr(threshold);
                return compare(operator, thresholdValue, levelValue);
            }
        }
        return null;
    }

    /**
     * check if currency code and unit matches ( $ M K )
     * @param value
     * @param hint
     * @return
     */
    private static Boolean validateCurrencyAndUnit(String value, String hint){
        String currency = String.valueOf(hint.charAt(0));
        String unit = String.valueOf(hint.charAt(hint.length() - 1));
        if(value.startsWith(currency) && value.endsWith(unit)){
            return true;
        } else {
            return false;
        }
    }

    private static Boolean calculateWithoutLogicalOperator(String threshold, String level, String hint){
        if(hint.startsWith("percentage") || hint.startsWith("number")){
            return calculateNumberCase(threshold, level);
        } else if(hint.contains("$$mm") || hint.contains("$$k")){
            return calculateCurrencyNumberCase(threshold, level, hint);
        }
        return null;
    }

    /**
     * compare threshold and level by operator
     * below is how to deal with no operator :
     Non-negative example: Credit  Bank-Wide  Criticized Loans Ratio
     <20% = Green; >20% and <25% = Amber; >25% = Red
     Negative example: Credit  Bank-Wide
     >(20%) = Green; <(20%) and >(25%) = Amber; <(25%) = Red
     * @param operator
     * @param threshold
     * @param value
     * @return
     */
    private static Boolean compare(String operator, Double threshold, Double value){
        if(threshold == null || value == null){
            return null;
        }
        if(operator != null){
            switch (operator){
                case ">=" :
                case "≥" : {
                    return value >= threshold;
                }
                case "<=" :
                case "≤" : {
                    return value <= threshold;
                }
                case ">" : {
                    return value > threshold;
                }
                case "<" : {
                    return value < threshold;
                }
            }
            return null;
        } else {
            if(threshold < 0){
                return value <= threshold;
            } else {
                return value >= threshold;
            }
        }
    }

}
