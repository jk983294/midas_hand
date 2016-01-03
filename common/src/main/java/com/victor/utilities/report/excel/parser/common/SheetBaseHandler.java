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