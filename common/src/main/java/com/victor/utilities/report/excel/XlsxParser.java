package com.victor.utilities.report.excel;

/**
 * Created by Administrator on 2015/11/25.
 */
public class XlsxParser {
}

package com.victor.utilities.report.excel.model;


public class AppMetadata {

    private String a, b, c;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}


package com.victor.utilities.report.excel.model;

public class AppModel {

    private String field1, groupInfo, valueString, dataHint;

    private Integer order, cob;

    private Double field3, value;

    private ColorTag colorTag;

    private int decimal;
    private NumberFormat numberFormat;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getCob() {
        return cob;
    }

    public void setCob(Integer cob) {
        this.cob = cob;
    }

    public void setField3(Double field3) {
        this.field3 = field3;
    }

    public double getField3() {
        return field3;
    }

    public void setField3(double field3) {
        this.field3 = field3;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public ColorTag getColorTag() {
        return colorTag;
    }

    public void setColorTag(ColorTag colorTag) {
        this.colorTag = colorTag;
    }

    public String getDataHint() {
        return dataHint;
    }

    public void setDataHint(String dataHint) {
        this.dataHint = dataHint;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public AppModel() {

    }
}

package com.victor.utilities.report.excel.model;

public enum ColorTag {
    Red,
    Amber,
    Green,
    Blank,
    Unknown
}

package com.victor.utilities.report.excel.model;

public enum NumberFormat {
    // $10,000 -- $10 -- $10K
    FullNumber,
    Shorten,
    Suffix
}

package com.victor.utilities.report.excel.model;

public enum NumberMagnitude {
    Bn,
    M,
    K,
    underK
}

package com.victor.utilities.report.excel.util;

import com.victor.utilities.report.excel.model.AppModel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * for report purpose, display order need to reorder
 */
public class AppSortingUtil {

    private static final Logger logger = LoggerFactory.getLogger(AppSortingUtil.class);

    private static final String[] field1s = new String[]{"aa", "bb", "cc", "dd", "ee"};
    private static final Pattern[] field1Patterns = new Pattern[field1s.length];

    static {
        for(int i = 0; i < field1s.length; ++i){
            field1Patterns[i] = Pattern.compile(field1s[i]);
        }
    }

    /**
     * sort by Field1
     * field12groupInfo2appModels must be a LinkedHashMap
     * @param field12groupInfo2appModels
     * @return
     */
    public static void getDataSortedByField1(Map<String, Map<String, List<AppModel>>> field12groupInfo2appModels){
        KeyValue<String, Map<String, List<AppModel>>>[] data = new KeyValue[field1s.length];
        Map<String, Map<String, List<AppModel>>> missing = new LinkedHashMap<>();
        boolean hasData = false;
        for(Map.Entry<String, Map<String, List<AppModel>>> entry : field12groupInfo2appModels.entrySet()) {
            String field1 = entry.getKey();
            int index = getIndex(field1Patterns, field1);
            if(index >= 0){
                data[index] = new KeyValue<>(field1, entry.getValue());
                hasData = true;
            } else {
                missing.put(field1, entry.getValue());
                logger.error("can not find field1 order : " + field1);
            }
        }
        if(hasData){
            field12groupInfo2appModels.clear();
            for(KeyValue<String, Map<String, List<AppModel>>> a : data){
                if(a != null){
                    field12groupInfo2appModels.put(a.getKey(), a.getValue());
                }
            }
            field12groupInfo2appModels.putAll(missing);
        }
    }

    /**
     * sort appModels list by order
     */
    public static void sortByOrder(List<AppModel> appModels){
        if(CollectionUtils.isNotEmpty(appModels)){
            Collections.sort(appModels, new OrderComparator());
        }
    }

    private static class OrderComparator implements Comparator<AppModel> {
        @Override
        public int compare(AppModel o1, AppModel o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }

    private static int getIndex(String[] data, String toFind){
        toFind = toFind.toLowerCase().trim();
        for(int i = 0; i < data.length; ++i){
            if(toFind.contains(data[i])){
                return i;
            }
        }
        return -1;
    }

    private static int getIndex(Pattern[] patterns, String toFind){
        toFind = toFind.toLowerCase().trim();
        for(int i = 0; i < patterns.length; ++i){
            if(RegExpHelper.contains(toFind, patterns[i])){
                return i;
            }
        }
        return -1;
    }

}

package com.victor.utilities.report.excel.util;

import java.util.Map;

/**
 * used for key value pair
 */
public class KeyValue<K, V> implements Map.Entry<K, V>
{
    private K key;
    private V value;

    public KeyValue(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return this.key;
    }

    public V getValue()
    {
        return this.value;
    }

    public K setKey(K key)
    {
        return this.key = key;
    }

    public V setValue(V value)
    {
        return this.value = value;
    }
}

package com.victor.utilities.report.excel.util;

import com.ms.gmr.credit.common.util.StringUtils;
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
    private static final String unitStr = "%|k|mm|m|bn|bps";
    private static final String numberUnitPatternStr = "((" + numberPatternStr + ")(\\s*("+unitStr+"))?)";
    private static final String currencyNumberTokenPatternStr = "(\\$\\s*)?(" + numberPatternStr + ")(\\s*(\\S+))?";

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
    private static final Pattern variablePattern = Pattern.compile("(\\w+(\\s*\\w+)*)\\s*=\\s*" + numberUnitPatternStr);
    private static final Pattern arithmeticPattern = Pattern.compile("\\w+\\s*(\\+|-|\\*|/)\\s*" + numberUnitPatternStr + "\\s*(\\w+)?");
    private static final Pattern numberUnitPattern = Pattern.compile(numberUnitPatternStr);
    private static final Pattern footerPattern = Pattern.compile(".*(\\(\\d+\\))$");
    private static final Pattern currencyNumberTokenPattern = Pattern.compile(currencyNumberTokenPatternStr);

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
        while(matcher.find()){
            String numberStr = matcher.group(3);
            return getNumber(numberStr);
        }
        return null;
    }

    public static String extractFooter(String str){
        return extract(str, footerPattern);
    }

    /**
     * deal with percent character "%" conversion
     * @param number
     * @return
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

    public static List<String> extractCurrencyNumberTokenPattern(String str){
        if(StringUtils.isEmpty(str)) return null;
        if(str.contains("=")){
            str = str.substring(str.lastIndexOf("="));
        }
        if(StringUtils.isEmpty(str) || !containDigit(str)) return null;
        Matcher matcher = currencyNumberTokenPattern.matcher(str);
        List<String> results = new ArrayList<>();
        while(matcher.find()){
            results.add(matcher.group(2));  // number
            if(StringUtils.isNotEmpty(matcher.group(8))){
                results.add(matcher.group(8));  // token
            }
        }
        return results;
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

    public static boolean isFooter(String str){
        return str != null && footerPattern.matcher(str).matches();
    }

    public static boolean isCurrencyNumberToken(String str){
        return str != null && currencyNumberTokenPattern.matcher(str).matches();
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


package com.victor.utilities.report.excel.parser.common;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * base handler, based on official document example, make some modifications to reserve position for further handling
 */
public abstract class SheetBaseHandler extends DefaultHandler {

    public String content;          //content in cell
    public int row;                 //current cursor row
    public int column;              //current cursor column
    public String columnStr;        //current cursor column string representation
    private String position;        //position in sheet
    private String cellType;
    private String cellStyleStr;
    private xssfDataType nextDataType;
    // Set when V start element is seen
    private boolean vIsOpen;
    private StringBuilder value;

    protected TabTable tabTable;

    enum xssfDataType {
        BOOL,
        ERROR,
        FORMULA,
        INLINESTR,
        SSTINDEX,
        NUMBER,
    }

    /**
     * used to parse content in cell
     */
    private SharedStringsTable sst;
    private boolean nextIsString;
    public static final Pattern rowPattern = Pattern.compile("[0-9]+");
    public static final Pattern columnPattern = Pattern.compile("[A-Z]+");
    public static final Pattern datePattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public SheetBaseHandler(SharedStringsTable sst) {
        this.sst = sst;
        value = new StringBuilder();
        tabTable = new TabTable();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        if ("inlineStr".equals(name) || "v".equals(name) || "t".equals(name)) { // inlineStr seems never to appear here. "t" is used for inlineStr instead.
            vIsOpen = true;
            // Clear contents cache
            value.setLength(0);
        } else if (name.equals("c")) {
            position = attributes.getValue("r");
            // Figure out if the value is an index in the SST
            cellType = attributes.getValue("t");
            cellStyleStr = attributes.getValue("s");
            // Set up defaults.
            this.nextDataType = xssfDataType.NUMBER;
            if ("b".equals(cellType))
                nextDataType = xssfDataType.BOOL;
            else if ("e".equals(cellType))
                nextDataType = xssfDataType.ERROR;
            else if ("inlineStr".equals(cellType))
                nextDataType = xssfDataType.INLINESTR;
            else if ("s".equals(cellType)){
                nextDataType = xssfDataType.SSTINDEX;
            }
            else if ("str".equals(cellType))
                nextDataType = xssfDataType.FORMULA;
            else if (cellStyleStr != null) {
                // It's a number, but almost certainly one with a special style or format
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        // v => contents of a cell
        // Output after we've seen the string contents
        content = value.toString().trim();
        if (name.equals("c") && nextDataType == xssfDataType.INLINESTR) {
            parsePosition();
            tabTable.addCell(row, column, content);
            handleCellValue();
        }
        if (name.equals("v")) {
            if (nextDataType == xssfDataType.SSTINDEX) {
                int idx = Integer.parseInt(content);
                content = new XSSFRichTextString(sst.getEntryAt(idx)).toString().trim();
            }
            parsePosition();
            tabTable.addCell(row, column, content);
            handleCellValue();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (vIsOpen)
            value.append(ch, start, length);
    }

    /**
     * parse current cursor position
     */
    public void parsePosition(){
        Matcher matcher = rowPattern.matcher(position);
        if (matcher.find())
            row = Integer.valueOf(matcher.group(0));
        matcher = columnPattern.matcher(position);
        if (matcher.find()){
            columnStr = matcher.group(0);
            column = getColNum(columnStr);
        }
    }

    /**
     * user should implements this function to detail requirement
     * row and column are 1-based not 0
     */
    public abstract void handleCellValue();

    /**
     * convert alphabetic column to int
     * @param colName
     * @return
     */
    public int getColNum (String colName) {
        //remove any whitespace
        colName = colName.trim();
        StringBuffer buff = new StringBuffer(colName);
        //string to lower case, reverse then place in char array
        char chars[] = buff.reverse().toString().toLowerCase().toCharArray();
        int retVal=0, multiplier=0;
        for(int i = 0; i < chars.length;i++){
            //retrieve ascii value of character, subtract 96 so number corresponds to place in alphabet. ascii 'a' = 97
            multiplier = (int)chars[i]-96;
            //mult the number by 26^(position in array)
            retVal += multiplier * Math.pow(26, i);
        }
        return retVal;
    }

    public TabTable getTable() {
        return tabTable;
    }
}

package com.victor.utilities.report.excel.parser.common;

/**
 * represent tab cell data
 */
public class TabCell {

    private int rowIdx, columnIdx;

    private String content;

    public TabCell(int rowIdx, int columnIdx, String content) {
        this.rowIdx = rowIdx;
        this.columnIdx = columnIdx;
        this.content = content;
    }

    public TabCell() {
    }

    public int getRowIdx() {
        return rowIdx;
    }

    public void setRowIdx(int rowIdx) {
        this.rowIdx = rowIdx;
    }

    public int getColumnIdx() {
        return columnIdx;
    }

    public void setColumnIdx(int columnIdx) {
        this.columnIdx = columnIdx;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


package com.victor.utilities.report.excel.parser.common;


/**
 * represent tab row data
 */
public class TabRow {

    private TabCell cells[] = new TabCell[10];

    private int maxColumnIndex = -1;

    private int rowIdx;

    private int dataCount;

    public TabRow(int rowIdx) {
        this.rowIdx = rowIdx;
    }

    public void addCell(int rowIdx, int columnIdx, String content){
        if(this.rowIdx != rowIdx){
            throw new IllegalArgumentException("row index is not match");
        }
        maxColumnIndex = Math.max(maxColumnIndex, columnIdx);
        rangeCheckAndExpand(columnIdx);
        if(cells[columnIdx] == null){
            ++dataCount;
        }
        cells[columnIdx] = new TabCell(rowIdx, columnIdx, content);
    }

    public void addCell(int columnIdx, String content){
        addCell(rowIdx, columnIdx, content);
    }

    private void rangeCheckAndExpand(int index){
        if(index >= cells.length){
            TabCell newCells[] = new TabCell[index + 1];
            for (int i = 0; i < cells.length; ++i){
                newCells[i] = cells[i];
            }
            cells = newCells;
        }
    }

    public boolean containsData(){
        return dataCount > 0;
    }

    public boolean containsData(int index){
        if(index > maxColumnIndex){
            return false;
        } else {
            return cells[index] != null;
        }
    }

    public TabCell getCell(int columnIndex) {
        if(columnIndex < 0 || columnIndex > maxColumnIndex){
            return null;
        } else {
            return cells[columnIndex];
        }
    }

    public TabCell getCell(String columnIndex) {
        return getCell(getColNum(columnIndex));
    }

    public String getCellData(int columnIndex) {
        TabCell cell = getCell(columnIndex);
        return cell != null ? cell.getContent() : null;
    }

    public String getCellData(String columnIndex) {
        return getCellData(getColNum(columnIndex));
    }

    /**
     * convert alphabetic column to int
     * @param colName
     * @return
     */
    public int getColNum(String colName) {
        //remove any whitespace
        colName = colName.trim();
        StringBuffer buff = new StringBuffer(colName);
        //string to lower case, reverse then place in char array
        char chars[] = buff.reverse().toString().toLowerCase().toCharArray();
        int retVal=0, multiplier=0;
        for(int i = 0; i < chars.length;i++){
            //retrieve ascii value of character, subtract 96 so number corresponds to place in alphabet. ascii 'a' = 97
            multiplier = (int)chars[i]-96;
            //mult the number by 26^(position in array)
            retVal += multiplier * Math.pow(26, i);
        }
        return retVal;
    }

    public int getDataCount() {
        return dataCount;
    }

    public int getMaxColumnIndex() {
        return maxColumnIndex;
    }
}


package com.victor.utilities.report.excel.parser.common;

import org.apache.commons.lang.StringUtils;

/**
 * represent tab table data
 */
public class TabTable {

    private TabRow rows[] = new TabRow[60];

    private int maxRowIndex = -1, maxColumnIndex = -1;

    private int rowCount;

    public TabTable() {
    }

    public void addCell(int rowIdx, int columnIdx, String content){
        if(StringUtils.isEmpty(content)){
            return;
        }
        maxRowIndex = Math.max(maxRowIndex, rowIdx);
        maxColumnIndex = Math.max(maxColumnIndex, rowIdx);
        rangeCheckAndExpand(rowIdx);
        if(rows[rowIdx] == null){
            ++rowCount;
            rows[rowIdx] = new TabRow(rowIdx);
        }
        rows[rowIdx].addCell(rowIdx, columnIdx, content);
    }

    private void rangeCheckAndExpand(int index){
        if(index >= rows.length){
            TabRow newRows[] = new TabRow[index + 1];
            for (int i = 0; i < rows.length; ++i){
                newRows[i] = rows[i];
            }
            rows = newRows;
        }
    }

    public boolean containsRow(){
        return rowCount > 0;
    }

    public boolean containsRow(int index){
        if(index > maxRowIndex){
            return false;
        } else {
            return rows[index] != null;
        }
    }

    public TabRow getRow(int rowIndex){
        if(rowIndex < 0 || rowIndex > maxRowIndex){
            return null;
        } else {
            return rows[rowIndex];
        }
    }

    public TabCell getCell(int rowIndex, int columnIndex){
        TabRow row = getRow(rowIndex);
        if(row != null){
            return row.getCell(columnIndex);
        }
        return null;
    }

    public String getCellData(int rowIndex, int columnIndex){
        TabCell cell = getCell(rowIndex, columnIndex);
        if(cell != null){
            return cell.getContent();
        }
        return null;
    }

    public TabCell getCell(int rowIndex, String columnIndex){
        return getCell(rowIndex, getColNum(columnIndex));
    }

    public String getCellData(int rowIndex, String columnIndex){
        return getCellData(rowIndex, getColNum(columnIndex));
    }

    /**
     * convert alphabetic column to int
     * @param colName
     * @return
     */
    public int getColNum(String colName) {
        //remove any whitespace
        colName = colName.trim();
        StringBuffer buff = new StringBuffer(colName);
        //string to lower case, reverse then place in char array
        char chars[] = buff.reverse().toString().toLowerCase().toCharArray();
        int retVal=0, multiplier=0;
        for(int i = 0; i < chars.length;i++){
            //retrieve ascii value of character, subtract 96 so number corresponds to place in alphabet. ascii 'a' = 97
            multiplier = (int)chars[i]-96;
            //mult the number by 26^(position in array)
            retVal += multiplier * Math.pow(26, i);
        }
        return retVal;
    }

    public int getMaxRowIndex() {
        return maxRowIndex;
    }

    public int getMaxColumnIndex() {
        return maxColumnIndex;
    }
}


package com.victor.utilities.report.excel.parser.common;

import com.victor.utilities.report.excel.parser.common.SheetBaseHandler;
import com.victor.utilities.report.excel.model.AppMetadata;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.model.SharedStringsTable;

import java.util.*;

/**
 * provide basic validation utils
 */
public abstract class ValidationSheetHandler extends SheetBaseHandler {

    private final static int EMAIL_IMPORT_VALIDATION_THRESHOLD = 10;

    protected AppMetadata metadata;

    /**
     * convert columnStr (A, B, C etc.) to column header (FiledA, FiledB etc.)
     */
    protected Map<String, String> columnStr2HeaderMap;
    protected Set<String> fieldsSet = new HashSet<String>();
    /**
     * contain all error entry, used for email
     */
    protected StringBuilder errorMsg;
    /**
     * only contain several error entry, used for front end popup
     */
    protected List<String> frontEndErrorList;
    protected int errorCnt = 0;

    public ValidationSheetHandler(SharedStringsTable sst, AppMetadata metadata) {
        super(sst);
        this.metadata = metadata;
        errorMsg =  new StringBuilder();
        frontEndErrorList = new ArrayList<String>();
        columnStr2HeaderMap = new HashMap<String, String>();
    }

    /**
     * get all pre-defined mandatory fields
     * @return
     */
    protected abstract Set<String> getMandatoryFields();

    public abstract void validate();

    public String getErrorMsg(){
        return errorMsg.toString();
    }

    public String getFrontEndError() {
        StringBuilder msg = new StringBuilder("");
        for(String errorEntry : frontEndErrorList){
            msg.append(errorEntry).append("\n");
        }
        if(errorCnt > EMAIL_IMPORT_VALIDATION_THRESHOLD){
            msg.append("Please check more detail in email.");
        }
        return msg.toString();
    }

    /**
     * check if mandatory fields included
     */
    protected void validateMandatoryFields(){
        String missedColumns = "";
        Set<String> mandatoryFieldsSet = getMandatoryFields();
        for(String key : mandatoryFieldsSet){
            if(!fieldsSet.contains(key)){
                missedColumns += key + ", ";
            }
        }
        if(StringUtils.isNotEmpty(missedColumns)){
            addErrorMsg("Missing mandatory header : < " + missedColumns.substring(0, missedColumns.lastIndexOf(",")) + " >");
        }
    }

    /**
     * check metadata against data collected from excel
     */
    protected void validateMetadata(){
        //
    }

    protected void addErrorMsg(String msg){
        if(StringUtils.isEmpty(msg)){
            return;
        }
        if(frontEndErrorList.size() < EMAIL_IMPORT_VALIDATION_THRESHOLD){
            frontEndErrorList.add(msg);
        }
        errorMsg.append(msg).append("\n");
        ++errorCnt;
    }

    protected void addErrorMsg(String msg, int rowNumber){
        if(StringUtils.isEmpty(msg)){
            return;
        }
        addErrorMsg(String.format("Issue found in row %d : %s", rowNumber, msg));
    }

    protected void addErrorMsg(List<String> msgs, int rowNumber){
        if(msgs == null || msgs.size() <= 0){
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Issue").append(msgs.size() > 1 ? "s" : "").append(" found in row ").append(rowNumber).append(": ");
        for(String msg : msgs){
            stringBuilder.append(msg);
        }
        addErrorMsg(stringBuilder.toString());
    }

    public int getErrorCnt() {
        return errorCnt;
    }

    public Map<String, String> getColumnStr2HeaderMap() {
        return columnStr2HeaderMap;
    }

    public void setColumnStr2HeaderMap(Map<String, String> columnStr2HeaderMap) {
        this.columnStr2HeaderMap = columnStr2HeaderMap;
    }

    public Set<String> getFieldsSet() {
        return fieldsSet;
    }

    public void setFieldsSet(Set<String> fieldsSet) {
        this.fieldsSet = fieldsSet;
    }
}

package com.victor.utilities.report.excel.parser;

import com.victor.utilities.report.excel.parser.common.TabRow;
import com.victor.utilities.report.excel.parser.common.TabTable;
import com.victor.utilities.report.excel.model.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * map TabTable to AppModel list
 */
public class AppModelTableMapper {

    public static List<AppModel> map(TabTable table){
        List<AppModel> appModels = new ArrayList<AppModel>();
        String groupInfo = null;
        int maxRowIndex = table.getMaxRowIndex();
        for(int i = 0; i <= maxRowIndex; ++i){
            TabRow tabRow = table.getRow(i);
            if(tabRow == null || !tabRow.containsData()){   // empty row
                continue;
            } else if(tabRow.getDataCount() == 1 && tabRow.getCell("E") != null){
                groupInfo = tabRow.getCellData("E");
            } else {
                AppModel appModel = new AppModel();
                appModel.setField1(table.getCellData(i, "A"));
                appModel.setCob((Double.valueOf(tabRow.getCellData("B")).intValue()));
                appModel.setField3(Double.valueOf(tabRow.getCellData("C")));
                appModel.setGroupInfo(groupInfo);
                appModels.add(appModel);
            }
        }
        return appModels;
    }

}

package com.victor.utilities.report.excel.parser;

import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.parser.common.ValidationSheetHandler;
import org.apache.poi.xssf.model.SharedStringsTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * extract exception rules
 */
public class SheetHandlerForAppData extends ValidationSheetHandler {

    public SheetHandlerForAppData(SharedStringsTable sst) {
        super(sst, null);
    }

    /**
     * extract counterparty data in Excel "Data" table
     */
    @Override
    public void handleCellValue() {
    }

    /**
     * get all pre-defined mandatory fields given legal entity
     * @return
     */
    @Override
    protected Set<String> getMandatoryFields(){
        Set<String> requiredFields = new HashSet<String>();
        return requiredFields;
    }

    @Override
    public void validate() {
        validateMandatoryFields();
    }

    public List<AppModel> getAppData() {
        return AppModelTableMapper.map(tabTable);
    }
}


package com.victor.utilities.report.excel;

import com.victor.utilities.report.excel.generator.AppDataReport;
import com.victor.utilities.report.excel.generator.common.ReportException;
import com.victor.utilities.report.excel.model.AppMetadata;
import com.victor.utilities.report.excel.model.AppModel;

import javax.ws.rs.core.Response;
import java.util.List;

public class XlsxGenerator {

    /**
     * Generates the AppData report
     * @param filename - the filename to export to
     * @return Response - byte data of generated file
     * @throws ReportException
     */
    public static Response generateAppDataReport(AppMetadata metadata, List<AppModel> appModels, String filename) throws ReportException {
        return new AppDataReport(metadata, appModels, filename).generateWorkbook();
    }

}


package com.victor.utilities.report.excel;

import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.parser.SheetHandlerForAppData;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XlsxParser {

    private static Logger logger = LoggerFactory.getLogger(XlsxParser.class);

    /**
     * parse AppData excel file
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static List<AppModel> parseExceptionRules(InputStream inputStream) throws Exception {
        logger.info("Parse excel start...");
        List<AppModel> data = new ArrayList<AppModel>();
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");

        SheetHandlerForAppData handler = new SheetHandlerForAppData(sst);
        parser.setContentHandler((ContentHandler)handler);

        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) r.getSheetsData();
        if(sheets.hasNext()){
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            return handler.getAppData();
        }
        return data;
    }
}
