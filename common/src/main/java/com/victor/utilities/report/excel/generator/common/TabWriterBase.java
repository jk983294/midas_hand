package com.victor.utilities.report.excel.generator.common;

import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.model.ColorTag;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * base class for sheet generation template
 */
public abstract class TabWriterBase {

    private final static Logger logger = LoggerFactory.getLogger(TabWriterBase.class);

    protected XSSFWorkbook wb;
    protected XSSFSheet sheet;
    protected XSSFDataFormat formatter;
    protected XSSFCellStyle headerStyle, subHeaderStyle, subSubHeaderStyle;
    protected XSSFCellStyle defaultStyle, stringDefaultStyle;
    protected XSSFCellStyle boldStyle;
    protected XSSFCellStyle hiddenStyle;
    protected XSSFCellStyle percentStyle, dateStyle, intStyle, bigDoubleStyle, smallDoubleStyle;
    protected XSSFCellStyle greenStyle;
    protected XSSFCellStyle amberStyle;
    protected XSSFCellStyle redStyle;
    protected XSSFCellStyle noColorStyle;
    protected XSSFFont boldFont, superscriptFont, superscriptHintFont;
    protected XSSFColor hintColor = new XSSFColor(new java.awt.Color(0, 112, 192));
    protected XSSFColor greenColor, amberColor, redColor;
    protected int rowIdx;
    protected int colIdx;
    protected Map<String, String> queryParams;          // the search params
    protected Map<Integer, Double> column2avgSize;        // record every column's max size
    protected Map<Integer, Double> column2SizeManually;        // record every column's max size
    protected Map<Integer, Integer> column2count;        // record the content count for each column
    protected Row headerRow;
    protected String sheetName;
    protected AppCellStyles styles[] = new AppCellStyles[3];


    private final Pattern intPattern = Pattern.compile("^\\d+$");
    private final Pattern doublePattern = Pattern.compile("[-+]?\\s*(\\d+\\.\\d*|\\d*\\.\\d+|\\d+)([eE][-+]?\\d+)?");
    private final static int charSize = 320;             // used to calculate column size
    private final static int MAX_CHAR_COUNT_PER_CELL = 82;
    private final static int MIN_CHAR_COUNT_PER_CELL = 25;
    private final static int MAX_CHAR_SIZE = 255 * MAX_CHAR_COUNT_PER_CELL;
    private final static int MIN_CHAR_SIZE = 255 * MIN_CHAR_COUNT_PER_CELL;
    private final static int SIGNIFICANT_DIGIT_LENGTH_FOR_SMALL_DOUBLE = 4;
    private final static int SIGNIFICANT_DIGIT_LENGTH_FOR_BIG_DOUBLE = 2;

    private final static String percentFormatStr = "0.0%;(0.0%);0%;@";
    private final static String intFormatStr = "#,###;(#,###);0;@";
    private final static String smallDoubleFormatStr = "0.0;(0.0);0;@";
    private final static String bigDoubleFormatStr = "#,##0.0;(#,##0.0);0;@";

    public TabWriterBase(XSSFWorkbook wb) {
        this.wb = wb;
        preInitCommonTemplateCellStyles();
        initOwnCellStyles();
        postInitCommonTemplateCellStyles();
    }

    /**
     * Generates excel export
     * @return Response - byte data of generated file
     */
    public void generate(String sheetName, int sheetIndex, Object data) throws ReportException {
        sheet = wb.createSheet();
        this.sheetName = sheetName;
        wb.setSheetName(sheetIndex, sheetName);
        column2avgSize = new HashMap<Integer, Double>();
        column2SizeManually = new HashMap<Integer, Double>();
        column2count = new HashMap<Integer, Integer>();
        rowIdx = colIdx = 0;

        generateSheetTab(data);

        autoSizeColumn(sheet);
    }

    /**
     * data is passed in, in this way, tab writer is stateless for reuse
     * implementor need to convert data into proper data
     * @param data
     */
    protected abstract void generateSheetTab(Object data) throws ReportException;

    /**
     * need to init its own style set
     */
    protected abstract void initOwnCellStyles();

    /**
     * init cell style
     */
    private void preInitCommonTemplateCellStyles() {
        formatter = wb.createDataFormat();
        boldFont = wb.createFont();
        boldFont.setColor(IndexedColors.BLACK.getIndex());
        boldFont.setBold(true);
        superscriptFont = wb.createFont();
        superscriptFont.setTypeOffset(Font.SS_SUPER);

        superscriptHintFont = wb.createFont();
        superscriptHintFont.setTypeOffset(Font.SS_SUPER);
        superscriptHintFont.setColor(hintColor);

        defaultStyle = wb.createCellStyle();
        defaultStyle.setWrapText(true);
        defaultStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        defaultStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        defaultStyle.setBorderBottom(CellStyle.BORDER_THIN);
        defaultStyle.setBorderTop(CellStyle.BORDER_THIN);
        defaultStyle.setBorderLeft(CellStyle.BORDER_THIN);
        defaultStyle.setBorderRight(CellStyle.BORDER_THIN);
        XSSFColor borderColor = new XSSFColor(new java.awt.Color(166, 166, 166));
        defaultStyle.setBottomBorderColor(borderColor);
        defaultStyle.setTopBorderColor(borderColor);
        defaultStyle.setLeftBorderColor(borderColor);
        defaultStyle.setRightBorderColor(borderColor);
    }

    /**
     * after sub class init RAG color cell style, init int, double, percent version
     * for default style could change, move string, number, percent style to here
     */
    private void postInitCommonTemplateCellStyles(){
        noColorStyle = getCloneCellStyle(null);
        stringDefaultStyle = getCloneCellStyle(defaultStyle);
        stringDefaultStyle.setAlignment(CellStyle.ALIGN_LEFT);
        stringDefaultStyle.setIndention((short)1);
        //format trick to hide zero : 0;-0;;@
        percentStyle = getCloneCellStyle(defaultStyle);
        percentStyle.setDataFormat(formatter.getFormat(percentFormatStr));
        intStyle = getCloneCellStyle(defaultStyle);
        intStyle.setDataFormat(formatter.getFormat(intFormatStr));
        bigDoubleStyle = getCloneCellStyle(defaultStyle);
        bigDoubleStyle.setDataFormat(formatter.getFormat(bigDoubleFormatStr));
        smallDoubleStyle = getCloneCellStyle(defaultStyle);
        smallDoubleStyle.setDataFormat(formatter.getFormat(smallDoubleFormatStr));
        dateStyle = getCloneCellStyle(defaultStyle);
        dateStyle.setDataFormat(formatter.getFormat("#"));
        dateStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);

        hiddenStyle = getCloneCellStyle(defaultStyle);
        hiddenStyle.setHidden(true);

        boldStyle = getCloneCellStyle(defaultStyle);
        boldStyle.setFont(boldFont);

        for(int i = 0; i < 3; i++){
            styles[i] = new AppCellStyles(i, wb, hintColor, greenColor, amberColor, redColor, defaultStyle);
        }
    }

    protected XSSFCellStyle getCloneCellStyle(XSSFCellStyle copy){
        XSSFCellStyle style = wb.createCellStyle();
        style.cloneStyleFrom(copy == null ? defaultStyle : copy);
        return style;
    }

    /**
     * set max column size
     * @param column
     * @param len
     */
    protected void  setColumnSize(int column, int len){
        if(column2avgSize.get(column) == null){
            column2avgSize.put(column, (double)len * charSize);
            column2count.put(column, 1);
        } else {
            int count = column2count.get(column);
            column2avgSize.put(column, (column2avgSize.get(column) * count + len * charSize)/ (count + 1));
            column2count.put(column, count + 1);
        }
    }

    /**
     * manually set column char size
     * @param column
     * @param len
     */
    protected void  setColumnSizeManually(int column, int len){
        column2SizeManually.put(column, (double)len * charSize);
    }

    /**
     * Auto-size column widths.
     * Note: sheet.autoSizeColumn(int i) is apparently slow for larger tables.
     * @param sheet - spreadsheet to be autosize
     */
    private void autoSizeColumn(Sheet sheet){
        //Auto size not work for stream mode, so we need to maintain the column size and set it manually
        if(column2avgSize != null && column2avgSize.size() != 0){
            for (Map.Entry<Integer,Double> size : column2avgSize.entrySet()){
                Integer columnSize = Math.max( MIN_CHAR_SIZE, Math.min(MAX_CHAR_SIZE, size.getValue().intValue()));
                sheet.setColumnWidth(size.getKey(), columnSize);
            }
        }
        if(column2SizeManually != null && column2SizeManually.size() != 0){
            for (Map.Entry<Integer,Double> size : column2SizeManually.entrySet()){
                sheet.setColumnWidth(size.getKey(), size.getValue().intValue());
            }
        }
    }

    protected void setCellSumFormula(Row row, int column, int start, int end){
        if(start <= end){
            Cell cell = row.createCell(column);
            String columnLetter = CellReference.convertNumToColString(column);
            StringBuilder formula = new StringBuilder("SUM(");
            formula.append(columnLetter).append(start).append(":").append(columnLetter).append(end).append(")");
            cell.setCellFormula(formula.toString());
        }
    }

    protected void setCellSumFormula(Row row, int column, int start, int end, CellStyle cellStyle){
        if(start <= end){
            Cell cell = row.createCell(column);
            String columnLetter = CellReference.convertNumToColString(column);
            StringBuilder formula = new StringBuilder("SUM(");
            formula.append(columnLetter).append(start).append(":").append(columnLetter).append(end).append(")");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * autoSizeColumn needs every cell is not null, so if meet null use "" instead
     * @param row - row to be set
     * @param column - column in row to create
     * @param data - data to be populated
     */
    protected void setCell(Row row, int column, Double data){
        if (data != null){
            Cell cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(data);
            if(Math.abs(data) <= 1.0){
                cell.setCellStyle(smallDoubleStyle);
                setColumnSize(column, numberLength(data, SIGNIFICANT_DIGIT_LENGTH_FOR_SMALL_DOUBLE));
            } else {
                cell.setCellStyle(bigDoubleStyle);
                setColumnSize(column, numberLength(data, SIGNIFICANT_DIGIT_LENGTH_FOR_BIG_DOUBLE));
            }
        }  else {
            setCell(row, column, defaultStyle);
        }
    }

    protected void setCell(Row row, int column, Integer data){
        if (data != null){
            Cell cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(data);
            cell.setCellStyle(intStyle);
            setColumnSize(column, numberLength(data));
        }  else {
            setCell(row, column, defaultStyle);
        }
    }

    /**
     * in case integer overflow, use Double.valueOf
     */
    protected void setCellInteger(Row row, int column, Double data){
        if (data != null){
            Cell cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(data);
            cell.setCellStyle(intStyle);
            if(Math.abs(data) <= 1.0){
                setColumnSize(column, numberLength(data, SIGNIFICANT_DIGIT_LENGTH_FOR_SMALL_DOUBLE));
            } else {
                setColumnSize(column, numberLength(data, SIGNIFICANT_DIGIT_LENGTH_FOR_BIG_DOUBLE));
            }
        }  else {
            setCell(row, column, defaultStyle);
        }
    }

    /**
     * new entry for level value
     */
    protected void setCellLevel(Row row, int column, String valueText, Double value, AppModel appModel) throws ReportException {
        String hint = appModel.getDataHint();
        ColorTag tag = appModel.getColorTag();
        AppCellStyles cellStyles = getCellStylesForLevel(appModel);
        if(value == null){ // last try
            value = RegExpHelper.getNumber(valueText);
        }
        XSSFCellStyle style = cellStyles.getStyle(value, tag, hint);
        if(value != null){
            setCell(row, column, value, style);
        } else if(valueText != null){
            setCell(row, column, valueText, style);
        } else {
            setCell(row, column, style);
        }
    }

    /**
     * entry for hint data
     */
    protected void setCellHint(Row row, int column, AppModel appModel) throws ReportException {
        String hint = appModel.getDataHint();
        AppCellStyles cellStyles = getCellStylesForLevel(appModel);
        XSSFCellStyle style = cellStyles.getHintStyle(hint);

        if(hint != null){
            setCell(row, column, hint, style);
        } else {
            setCell(row, column, style);
        }
    }

    private AppCellStyles getCellStylesForLevel(AppModel appModel) throws ReportException {
        if(appModel.getDecimal() <= 2 && appModel.getDecimal() >= 0){
            AppCellStyles cellStyles = styles[appModel.getDecimal()];
            cellStyles.numberFormat = appModel.getNumberFormat();
            return cellStyles;
        } else {
            logger.error("Decimal overflow: " + appModel.getDecimal());
            throw new ReportException("Decimal overflow: " + appModel.getDecimal());
        }
    }

    protected void setCell(Row row, int column, String data){
        if (data != null){
            if( intPattern.matcher(data).matches()){
                setCellInteger(row, column, Double.valueOf(data));
            } else if( doublePattern.matcher(data).matches()){
                setCell(row, column, Double.valueOf(data));
            } else  {
                setCell(row, column, data, defaultStyle);
            }
        } else {
            setCell(row, column, defaultStyle);
        }
    }

    protected void setCell(Row row, int column, String data, CellStyle cellStyle){
        if(data != null){
            if(RegExpHelper.isFooter(data)){
                setCellSuperscript(row, column, data, cellStyle, false);
            } else {
                Cell cell = row.createCell(column, Cell.CELL_TYPE_STRING);
                cell.setCellValue(data);
                cell.setCellStyle(cellStyle);
            }
            setColumnSize(column, data.length());
        } else {
            setCell(row, column, cellStyle);
        }
    }

    protected void setCellSuperscript(Row row, int column, String data, CellStyle cellStyle, boolean isHint){
        String footer = RegExpHelper.extractFooter(data);
        Cell cell = row.createCell(column, Cell.CELL_TYPE_STRING);
        cell.setCellStyle(cellStyle);
        //rich text consists of one run overriding the cell style
        XSSFRichTextString richString = new XSSFRichTextString(data);
        richString.applyFont(data.lastIndexOf(footer), data.length(), isHint ? superscriptHintFont: superscriptFont);
        cell.setCellValue( richString );
    }

    protected void setCell(Row row, int column, Integer data, CellStyle cellStyle){
        if(data != null){
            Cell cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(data);
            cell.setCellStyle(cellStyle);
            setColumnSize(column, numberLength(data));
        } else {
            setCell(row, column, cellStyle);
        }
    }

    protected void setCell(Row row, int column, Double data, CellStyle cellStyle){
        if(data != null){
            Cell cell = row.createCell(column, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(data);
            cell.setCellStyle(cellStyle);
            if(Math.abs(data) <= 1.0){
                setColumnSize(column, numberLength(data, SIGNIFICANT_DIGIT_LENGTH_FOR_SMALL_DOUBLE));
            } else {
                setColumnSize(column, numberLength(data, SIGNIFICANT_DIGIT_LENGTH_FOR_BIG_DOUBLE));
            }
        } else {
            setCell(row, column, cellStyle);
        }
    }

    protected void setCellRange(Row row, int from, int to, CellStyle cellStyle){
        for(int i = from; i <= to; ++i){
            setCell(row, i, cellStyle);
        }
    }

    protected void setCell(Row row, int column, CellStyle cellStyle){
        Cell cell = row.createCell(column, Cell.CELL_TYPE_STRING);
        cell.setCellStyle(cellStyle);
    }
    
    /**
     * dropdown list
     */
    protected void setDropdownCell(int rowIndex, int columnIndex, String[] options){
        setDropdownCell(rowIndex, rowIndex, columnIndex, columnIndex, options);
    }

    protected void setDropdownCell(int rowIndexFrom, int rowIndexTo, int columnIndexFrom, int columnIndexTo, String[] options){
        if(options != null && options.length > 0){
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
            CellRangeAddressList addressList = new CellRangeAddressList(rowIndexFrom, rowIndexTo, columnIndexFrom, columnIndexTo);
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setSuppressDropDownArrow(true);
            sheet.addValidationData(dataValidation);
        }
    }
    
    protected void setDropdownCell(int rowIndexFrom, int rowIndexTo, int columnIndexFrom, int columnIndexTo,
                                   XSSFDataValidationHelper validationHelper, DataValidationConstraint constraint){
        if(constraint == null) return;
        CellRangeAddressList addressList = new CellRangeAddressList(rowIndexFrom, rowIndexTo, columnIndexFrom, columnIndexTo);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(dataValidation);
    }

    /**
     * calculate number digit length
     * for double, need add three more, one for dot, two for significant digits
     */
    private int numberLength(Double x, int significant){
        return ((Math.abs(x) < 1e-6 ? 1 : (1 + (int)Math.floor(Math.log10(Math.abs(x))))) + significant) + 2;
    }

    private int numberLength(Integer x){
        return x==0 ? 1 : (1 + (int)Math.floor(Math.log10(Math.abs(x))));
    }

    public static Double getDoubleValue(String data){
        if(data == null) return null;
        try {
            return Double.valueOf(data);
        } catch (Exception e){
            return null;
        }
    }
}
