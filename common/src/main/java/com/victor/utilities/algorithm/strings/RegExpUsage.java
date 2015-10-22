package com.victor.utilities.algorithm.strings;

import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * demo for RegExpHelper
 */
public class RegExpUsage {

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
        Double levelValue = RegExpHelper.getNumber(level), thresholdValue;
        if(levelValue == null){
            return null;
        }
        int relationalOperatorCount = RegExpHelper.numberRelationalOperatorCount(threshold);
        int currencyNumberUnitCount = RegExpHelper.currencyNumberUnitCount(threshold);
        if(currencyNumberUnitCount == 1){
            String a = RegExpHelper.extractCurrencyNumberUnitStr(threshold);
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
