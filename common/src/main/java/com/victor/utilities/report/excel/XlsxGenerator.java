package com.victor.utilities.report.excel;

/**
 * Created by Administrator on 2015/11/25.
 */
public class XlsxGenerator {
}


package com.victor.utilities.report.excel.generator.common;


public class ReportException extends Exception {
    private static final long serialVersionUID = 16546543L;

    public ReportException() {
        super();
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable cause) {
        super(cause);
    }

}


package com.victor.utilities.report.excel.generator.common;

import com.victor.utilities.report.excel.generator.common.ReportException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * base class for spreadsheet generation template, stream mode
 */
public abstract class ReportXssfBase {

    private static Logger logger = LoggerFactory.getLogger(ReportXssfBase.class);

    protected XSSFWorkbook wb;

    protected String filename;                          // the filename to export to

    public ReportXssfBase(String filename) {
        this.filename = filename;
        wb = new XSSFWorkbook();
    }

    /**
     * Generates excel export
     * @return Response - byte data of generated file
     * @throws com.victor.utilities.report.excel.generator.common.ReportException
     */
    public Response generateWorkbook() throws ReportException {
        try {

            writeTabs();

            byte[] data = write(wb);
            return createResponse(data, filename, "application/excel");
        }catch (Exception ie) {
            logger.error("generate workbook failed.", ie);
            throw new ReportException(ie);
        }
    }

    /**
     * sub class need to implement how to write each tab
     */
    public abstract void writeTabs() throws ReportException;


    /**
     * convert Workbook to byte array
     * @param workbook
     * @return
     * @throws ReportException
     */
    private byte[] write(Workbook workbook) throws ReportException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            return stream.toByteArray();
        } catch (IOException e) {
            logger.error("Error during xls creation", e);
            throw new ReportException("Error during xls creation", e);
        }
    }

    private Response createResponse(byte[] rawBytes, String filename, String contentType) {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.header("Content-Type", contentType);
        responseBuilder.header("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        responseBuilder.header("Access-Control-Expose-Headers", "Content-Disposition");
        responseBuilder.entity(rawBytes);
        return responseBuilder.build();
    }

}

package com.victor.utilities.report.excel.generator.common;

import com.victor.utilities.report.excel.model.ColorTag;
import com.victor.utilities.report.excel.model.NumberFormat;
import com.victor.utilities.report.excel.model.NumberMagnitude;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

/**
 * contains cell styles for one type
 */
public class RgfCellStyles {

    public XSSFCellStyle defaultStyle, percentStyle, numberStyle;
    public XSSFFont boldFont;
    protected XSSFDataFormat formatter;

    public int decimal = 0;

    public NumberFormat numberFormat = NumberFormat.FullNumber;

    protected XSSFWorkbook wb;

    public XSSFCellStyle greenStyle, greenNumberStyle, greenNumberCurrencyStyle, greenPercentStyle, greenBnStyle, greenMStyle, greenKStyle, greenBnShortenStyle, greenMShortenStyle, greenKShortenStyle;
    public XSSFCellStyle amberStyle, amberNumberStyle, amberNumberCurrencyStyle, amberPercentStyle, amberBnStyle, amberMStyle, amberKStyle, amberBnShortenStyle, amberMShortenStyle, amberKShortenStyle;
    public XSSFCellStyle redStyle, redNumberStyle, redNumberCurrencyStyle, redPercentStyle, redBnStyle, redMStyle, redKStyle, redBnShortenStyle, redMShortenStyle, redKShortenStyle;
    public XSSFCellStyle noColorStyle, noColorNumberStyle, noColorNumberCurrencyStyle, noColorPercentStyle, noColorBnStyle, noColorMStyle, noColorKStyle, noColorBnShortenStyle, noColorMShortenStyle, noColorKShortenStyle;
    public XSSFCellStyle hintStyle, hintNumberStyle, hintNumberCurrencyStyle, hintPercentStyle, hintBnStyle, hintMStyle, hintKStyle, hintBnShortenStyle, hintMShortenStyle, hintKShortenStyle;

    public XSSFColor hintColor, greenColor, amberColor, redColor;

    private final static String percentFormatStr[] = {"#,###%;(#,###%);0%;@", "#,##0.0%;(#,##0.0%);0.0%;@", "#,##0.00%;(#,##0.00%);0.00%;@"};
    private final static String numberFormatStr[] = {"#,###;(#,###);0;@", "#,##0.0;(#,##0.0);0.0;@", "#,##0.00;(#,##0.00);0.00;@"};
    private final static String numberCurrencyFormatStr[] = {"$#,###;($#,###);$0;@", "$#,##0.0;($#,##0.0);$0.0;@", "$#,##0.00;($#,##0.00);$0.00;@"};
    private final static String billionFormatStr[] = {"$#,###,,,\"Bn\";($#,###,,,\"Bn\");$0\"Bn\";@", "$#,##0.0,,,\"Bn\";($#,##0.0,,,\"Bn\");$0.0\"Bn\";@", "$#,##0.00,,,\"Bn\";($#,##0.00,,,\"Bn\");$0.00\"Bn\";@"};
    private final static String millionFormatStr[] = {"$#,###,,\"MM\";($#,###,,\"MM\");$0\"MM\";@", "$#,##0.0,,\"MM\";($#,##0.0,,\"MM\");$0.0\"MM\";@", "$#,##0.00,,\"MM\";($#,##0.00,,\"MM\");$0.00\"MM\";@"};
    private final static String kFormatStr[] = {"$#,###,\"K\";($#,###,\"K\");$0\"K\";@", "$#,##0.0,\"K\";($#,##0.0,\"K\");$0.0\"K\";@", "$#,##0.00,\"K\";($#,##0.00,\"K\");$0.00\"K\";@"};
    private final static String billionShortenFormatStr[] = {"$#,###,,,;($#,###,,,);$0;@", "$#,##0.0,,,;($#,##0.0,,,);$0.0;@", "$#,##0.00,,,;($#,##0.00,,,);$0.00;@"};
    private final static String millionShortenFormatStr[] = {"$#,###,,;($#,###,,);$0;@", "$#,##0.0,,;($#,##0.0,,);$0.0;@", "$#,##0.00,,;($#,##0.00,,);$0.00;@"};
    private final static String kShortenFormatStr[] = {"$#,###,;($#,###,);$0;@", "$#,##0.0,;($#,##0.0,);$0.0;@", "$#,##0.00,;($#,##0.00,);$0.00;@"};

    public RgfCellStyles(int decimal, XSSFWorkbook wb, XSSFColor hintColor, XSSFColor greenColor, XSSFColor amberColor, XSSFColor redColor, XSSFCellStyle defaultStyle) {
        this.decimal = decimal;
        this.wb = wb;
        this.hintColor = hintColor;
        this.greenColor = greenColor;
        this.amberColor = amberColor;
        this.redColor = redColor;
        this.defaultStyle = defaultStyle;
        init();
    }

    public XSSFCellStyle getStyle(Double value, ColorTag tag, String hint){
        if(value != null && numberFormat != NumberFormat.FullNumber){
            return getStyle(tag, hint, getNumberMagnitude(value, hint));
        } else {
            return getStyle(tag, hint, null);
        }
    }

    public XSSFCellStyle getStyle(ColorTag tag, String hint, NumberMagnitude magnitude){
        if(tag != null){
            if(hint != null){
                hint = hint.toLowerCase().trim();
                if(hint.contains("percent")){
                    switch (tag){
                        case Green: return greenPercentStyle;
                        case Red: return redPercentStyle;
                        case Amber: return amberPercentStyle;
                        default: return noColorPercentStyle;
                    }
                } else if(hint.contains("number") || hint.contains("ratio")){ // ratio is for MSPBNA risk limits Liquidity Current Ratio
                    return getStyle(tag, magnitude, false);
                } else if(hint.contains("$")){
                    return getStyle(tag, magnitude, true);
                } else { // for text "red" "amber" "green"
                    switch (tag){
                        case Green: return greenStyle;
                        case Red: return redStyle;
                        case Amber: return amberStyle;
                    }
                }
            }
        } else {
            return getStyle(hint, magnitude);
        }
        return noColorStyle;
    }

    // for number with suffix
    public XSSFCellStyle getStyle(ColorTag tag, NumberMagnitude magnitude, boolean isCurrency){
        if(tag != null){
            if(magnitude != null){
                switch (magnitude){
                    case Bn: {
                        switch (tag){
                            case Green: return numberFormat == NumberFormat.Shorten ? greenBnShortenStyle : greenBnStyle;
                            case Red: return numberFormat == NumberFormat.Shorten ? redBnShortenStyle : redBnStyle;
                            case Amber: return numberFormat == NumberFormat.Shorten ? amberBnShortenStyle : amberBnStyle;
                            default: return numberFormat == NumberFormat.Shorten ? noColorBnShortenStyle : noColorBnStyle;
                        }
                    }
                    case M: {
                        switch (tag){
                            case Green: return numberFormat == NumberFormat.Shorten ? greenMShortenStyle : greenMStyle;
                            case Red: return numberFormat == NumberFormat.Shorten ? redMShortenStyle : redMStyle;
                            case Amber: return numberFormat == NumberFormat.Shorten ? amberMShortenStyle : amberMStyle;
                            default: return numberFormat == NumberFormat.Shorten ? noColorMShortenStyle : noColorMStyle;
                        }
                    }
                    case K: {
                        switch (tag){
                            case Green: return numberFormat == NumberFormat.Shorten ? greenKShortenStyle : greenKStyle;
                            case Red: return numberFormat == NumberFormat.Shorten ? redKShortenStyle : redKStyle;
                            case Amber: return numberFormat == NumberFormat.Shorten ? amberKShortenStyle : amberKStyle;
                            default: return numberFormat == NumberFormat.Shorten ? noColorKShortenStyle : noColorKStyle;
                        }
                    }
                }
            }
            if(isCurrency){
                switch (tag){   // if magnitude == null or under K
                    case Green: return greenNumberCurrencyStyle;
                    case Red: return redNumberCurrencyStyle;
                    case Amber: return amberNumberCurrencyStyle;
                    default: return noColorNumberCurrencyStyle;
                }
            } else {
                switch (tag){   // if magnitude == null or under K
                    case Green: return greenNumberStyle;
                    case Red: return redNumberStyle;
                    case Amber: return amberNumberStyle;
                    default: return noColorNumberStyle;
                }
            }
        } else {    // tag is null
            if(magnitude != null){
                switch (magnitude){
                    case Bn: return numberFormat == NumberFormat.Shorten ? noColorBnShortenStyle : noColorBnStyle;
                    case M: return numberFormat == NumberFormat.Shorten ? noColorMShortenStyle : noColorMStyle;
                    case K: return numberFormat == NumberFormat.Shorten ? noColorKShortenStyle : noColorKStyle;
                    default: return noColorNumberCurrencyStyle;
                }
            }
            return noColorStyle;
        }
    }

    // for tag is null
    public XSSFCellStyle getStyle(String hint, NumberMagnitude magnitude){
        if(hint != null){
            hint = hint.toLowerCase();
            if(hint.contains("number")){
                return noColorNumberStyle;
            } else if(hint.contains("percent")){
                return noColorPercentStyle;
            }
        }
        return noColorStyle;
    }

    private NumberMagnitude getNumberMagnitude(Double num, String hint){
        if(hint != null){
            hint = hint.trim().toLowerCase();
            if(hint.contains("$bn")){
                return NumberMagnitude.Bn;
            } else if(hint.contains("$m")){
                return NumberMagnitude.M;
            } else if(hint.contains("$k")){
                return NumberMagnitude.K;
            }
        }
        if(num != null){
            double digits = Math.log10(Math.abs(num) + 0.1);
            if(digits > 9){
                return NumberMagnitude.Bn;
            } else if(digits > 6){
                return NumberMagnitude.M;
            } else if(digits > 3){
                return NumberMagnitude.K;
            } else {
                return NumberMagnitude.underK;
            }
        }
        return null;
    }

    public XSSFCellStyle getHintStyle(String hint){
        if(hint != null){
            hint = hint.toLowerCase().trim();
            if(hint.contains("percent")){
                return hintPercentStyle;
            } else if(hint.contains("number") || hint.contains("ratio")){ // ratio is for MSPBNA risk limits Liquidity Current Ratio
                return hintNumberStyle;
            } else if(numberFormat == NumberFormat.FullNumber){
                if(hint.contains("$")){
                    return hintNumberCurrencyStyle;
                }
            } else { // suffix
                NumberMagnitude magnitude = getNumberMagnitude(null, hint);
                if(magnitude != null){
                    switch(magnitude){
                        case Bn: return numberFormat == NumberFormat.Shorten ? hintBnShortenStyle : hintBnStyle;
                        case M: return numberFormat == NumberFormat.Shorten ? hintMShortenStyle : hintMStyle;
                        case K: return numberFormat == NumberFormat.Shorten ? hintKShortenStyle : hintKStyle;
                        default: return hintNumberCurrencyStyle;
                    }
                }
            }
        }
        return hintStyle;
    }

    public void init(){
        formatter = wb.createDataFormat();

        greenStyle = getCloneCellStyle(null);
        greenStyle.setAlignment(CellStyle.ALIGN_CENTER);
        greenStyle.setFont(boldFont);
        greenStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        greenStyle.setFillForegroundColor(greenColor);
        amberStyle = getCloneCellStyle(greenStyle);
        amberStyle.setFillForegroundColor(amberColor);
        redStyle = getCloneCellStyle(greenStyle);
        redStyle.setFillForegroundColor(redColor);
        noColorStyle = getCloneCellStyle(null);

        percentStyle = getCloneCellStyle(defaultStyle);
        percentStyle.setDataFormat(formatter.getFormat(percentFormatStr[decimal]));
        numberStyle = getCloneCellStyle(defaultStyle);
        numberStyle.setDataFormat(formatter.getFormat(numberFormatStr[decimal]));

        hintStyle = getCloneCellStyle(defaultStyle);
        XSSFFont hintFont = wb.createFont();
        hintFont.setColor(hintColor);
        hintFont.setBold(true);
        hintStyle.setFont(hintFont);

        hintPercentStyle = getCloneCellStyle(hintStyle);
        hintPercentStyle.setDataFormat(formatter.getFormat(percentFormatStr[decimal]));
        hintNumberStyle = getCloneCellStyle(hintStyle);
        hintNumberStyle.setDataFormat(formatter.getFormat(numberFormatStr[decimal]));
        hintNumberCurrencyStyle = getCloneCellStyle(hintStyle);
        hintNumberCurrencyStyle.setDataFormat(formatter.getFormat(numberCurrencyFormatStr[decimal]));
        hintBnStyle = getCloneCellStyle(hintStyle);
        hintBnStyle.setDataFormat(formatter.getFormat(billionFormatStr[decimal]));
        hintMStyle = getCloneCellStyle(hintStyle);
        hintMStyle.setDataFormat(formatter.getFormat(millionFormatStr[decimal]));
        hintKStyle = getCloneCellStyle(hintStyle);
        hintKStyle.setDataFormat(formatter.getFormat(kFormatStr[decimal]));
        hintBnShortenStyle = getCloneCellStyle(hintStyle);
        hintBnShortenStyle.setDataFormat(formatter.getFormat(billionShortenFormatStr[decimal]));
        hintMShortenStyle = getCloneCellStyle(hintStyle);
        hintMShortenStyle.setDataFormat(formatter.getFormat(millionShortenFormatStr[decimal]));
        hintKShortenStyle = getCloneCellStyle(hintStyle);
        hintKShortenStyle.setDataFormat(formatter.getFormat(kShortenFormatStr[decimal]));

        if(greenStyle != null){
            greenNumberStyle = getCloneCellStyle(greenStyle);
            greenNumberStyle.setDataFormat(formatter.getFormat(numberFormatStr[decimal]));
            greenNumberCurrencyStyle = getCloneCellStyle(greenStyle);
            greenNumberCurrencyStyle.setDataFormat(formatter.getFormat(numberCurrencyFormatStr[decimal]));
            greenPercentStyle = getCloneCellStyle(greenStyle);
            greenPercentStyle.setDataFormat(formatter.getFormat(percentFormatStr[decimal]));
            greenBnStyle = getCloneCellStyle(greenStyle);
            greenBnStyle.setDataFormat(formatter.getFormat(billionFormatStr[decimal]));
            greenMStyle = getCloneCellStyle(greenStyle);
            greenMStyle.setDataFormat(formatter.getFormat(millionFormatStr[decimal]));
            greenKStyle = getCloneCellStyle(greenStyle);
            greenKStyle.setDataFormat(formatter.getFormat(kFormatStr[decimal]));
            greenBnShortenStyle = getCloneCellStyle(greenStyle);
            greenBnShortenStyle.setDataFormat(formatter.getFormat(billionShortenFormatStr[decimal]));
            greenMShortenStyle = getCloneCellStyle(greenStyle);
            greenMShortenStyle.setDataFormat(formatter.getFormat(millionShortenFormatStr[decimal]));
            greenKShortenStyle = getCloneCellStyle(greenStyle);
            greenKShortenStyle.setDataFormat(formatter.getFormat(kShortenFormatStr[decimal]));
        }

        if(amberStyle != null){
            amberNumberStyle = getCloneCellStyle(amberStyle);
            amberNumberStyle.setDataFormat(formatter.getFormat(numberFormatStr[decimal]));
            amberNumberCurrencyStyle = getCloneCellStyle(amberStyle);
            amberNumberCurrencyStyle.setDataFormat(formatter.getFormat(numberCurrencyFormatStr[decimal]));
            amberPercentStyle = getCloneCellStyle(amberStyle);
            amberPercentStyle.setDataFormat(formatter.getFormat(percentFormatStr[decimal]));
            amberBnStyle = getCloneCellStyle(amberStyle);
            amberBnStyle.setDataFormat(formatter.getFormat(billionFormatStr[decimal]));
            amberMStyle = getCloneCellStyle(amberStyle);
            amberMStyle.setDataFormat(formatter.getFormat(millionFormatStr[decimal]));
            amberKStyle = getCloneCellStyle(amberStyle);
            amberKStyle.setDataFormat(formatter.getFormat(kFormatStr[decimal]));
            amberBnShortenStyle = getCloneCellStyle(amberStyle);
            amberBnShortenStyle.setDataFormat(formatter.getFormat(billionShortenFormatStr[decimal]));
            amberMShortenStyle = getCloneCellStyle(amberStyle);
            amberMShortenStyle.setDataFormat(formatter.getFormat(millionShortenFormatStr[decimal]));
            amberKShortenStyle = getCloneCellStyle(amberStyle);
            amberKShortenStyle.setDataFormat(formatter.getFormat(kShortenFormatStr[decimal]));
        }

        if(redStyle != null){
            redNumberStyle = getCloneCellStyle(redStyle);
            redNumberStyle.setDataFormat(formatter.getFormat(numberFormatStr[decimal]));
            redNumberCurrencyStyle = getCloneCellStyle(redStyle);
            redNumberCurrencyStyle.setDataFormat(formatter.getFormat(numberCurrencyFormatStr[decimal]));
            redPercentStyle = getCloneCellStyle(redStyle);
            redPercentStyle.setDataFormat(formatter.getFormat(percentFormatStr[decimal]));
            redBnStyle = getCloneCellStyle(redStyle);
            redBnStyle.setDataFormat(formatter.getFormat(billionFormatStr[decimal]));
            redMStyle = getCloneCellStyle(redStyle);
            redMStyle.setDataFormat(formatter.getFormat(millionFormatStr[decimal]));
            redKStyle = getCloneCellStyle(redStyle);
            redKStyle.setDataFormat(formatter.getFormat(kFormatStr[decimal]));
            redBnShortenStyle = getCloneCellStyle(redStyle);
            redBnShortenStyle.setDataFormat(formatter.getFormat(billionShortenFormatStr[decimal]));
            redMShortenStyle = getCloneCellStyle(redStyle);
            redMShortenStyle.setDataFormat(formatter.getFormat(millionShortenFormatStr[decimal]));
            redKShortenStyle = getCloneCellStyle(redStyle);
            redKShortenStyle.setDataFormat(formatter.getFormat(kShortenFormatStr[decimal]));
        }

        if(noColorStyle != null){
            noColorNumberStyle = getCloneCellStyle(noColorStyle);
            noColorNumberStyle.setDataFormat(formatter.getFormat(numberFormatStr[decimal]));
            noColorNumberCurrencyStyle = getCloneCellStyle(noColorStyle);
            noColorNumberCurrencyStyle.setDataFormat(formatter.getFormat(numberCurrencyFormatStr[decimal]));
            noColorPercentStyle = getCloneCellStyle(noColorStyle);
            noColorPercentStyle.setDataFormat(formatter.getFormat(percentFormatStr[decimal]));
            noColorBnStyle = getCloneCellStyle(noColorStyle);
            noColorBnStyle.setDataFormat(formatter.getFormat(billionFormatStr[decimal]));
            noColorMStyle = getCloneCellStyle(noColorStyle);
            noColorMStyle.setDataFormat(formatter.getFormat(millionFormatStr[decimal]));
            noColorKStyle = getCloneCellStyle(noColorStyle);
            noColorKStyle.setDataFormat(formatter.getFormat(kFormatStr[decimal]));
            noColorBnShortenStyle = getCloneCellStyle(noColorStyle);
            noColorBnShortenStyle.setDataFormat(formatter.getFormat(billionShortenFormatStr[decimal]));
            noColorMShortenStyle = getCloneCellStyle(noColorStyle);
            noColorMShortenStyle.setDataFormat(formatter.getFormat(millionShortenFormatStr[decimal]));
            noColorKShortenStyle = getCloneCellStyle(noColorStyle);
            noColorKShortenStyle.setDataFormat(formatter.getFormat(kShortenFormatStr[decimal]));
        }
    }

    private XSSFCellStyle getCloneCellStyle(XSSFCellStyle copy){
        XSSFCellStyle style = wb.createCellStyle();
        style.cloneStyleFrom(copy == null ? defaultStyle : copy);
        return style;
    }

}

package com.victor.utilities.report.excel.generator.common;

import com.victor.utilities.report.excel.generator.common.RgfCellStyles;
import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.model.ColorTag;
import com.victor.utilities.report.excel.util.RegExpHelper;
import org.apache.poi.ss.usermodel.*;
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
    protected RgfCellStyles styles[] = new RgfCellStyles[3];


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
            styles[i] = new RgfCellStyles(i, wb, hintColor, greenColor, amberColor, redColor, defaultStyle);
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
        RgfCellStyles cellStyles = getCellStylesForLevel(appModel);
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
     * entry for hint
     */
    protected void setCellHint(Row row, int column, AppModel appModel) throws ReportException {
        String hint = appModel.getDataHint();
        RgfCellStyles cellStyles = getCellStylesForLevel(appModel);
        XSSFCellStyle style = cellStyles.getHintStyle(hint);

        if(hint != null){
            setCell(row, column, hint, style);
        } else {
            setCell(row, column, style);
        }
    }

    private RgfCellStyles getCellStylesForLevel(AppModel appModel) throws ReportException {
        if(appModel.getDecimal() <= 2 && appModel.getDecimal() >= 0){
            RgfCellStyles cellStyles = styles[appModel.getDecimal()];
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

package com.victor.utilities.report.excel.generator;

import com.victor.utilities.report.excel.generator.common.ReportException;
import com.victor.utilities.report.excel.generator.common.ReportXssfBase;
import com.victor.utilities.report.excel.generator.common.TabWriterBase;
import com.victor.utilities.report.excel.model.AppMetadata;
import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.util.AppSortingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates Appdata Report export
 * Both reports use the same basic format
 */
public class AppDataReport extends ReportXssfBase {

    private final static Logger logger = LoggerFactory.getLogger(AppDataReport.class);

    private  List<AppModel> appModels;

    private AppMetadata metadata;

    private Map<String, Map<String, List<AppModel>>> field12groupInfo2appModels;

    public AppDataReport(AppMetadata metadata, List<AppModel> appModels, String filename) {
        super(filename);
        this.metadata = metadata;
        this.appModels = appModels;
    }

    @Override
    public void writeTabs() throws ReportException {
        switch (metadata.getA()){
            case "a" : {
                aggregate();
                AppSortingUtil.getDataSortedByField1(field12groupInfo2appModels);
                int sheetIdx = 0;
                TabWriterBase tabWriter = new TabWriterForAppData(wb, metadata);
                tabWriter.generate("data", sheetIdx++, field12groupInfo2appModels);
            } break;
            case "b" : {

            } break;
            default: {
                logger.error("no a for " + metadata.getA());
                throw new ReportException("no a for " + metadata.getA());
            }
        }
    }

    /**
     * use TreeMap to get data sorted
     */
    private void aggregate(){
        field12groupInfo2appModels = new LinkedHashMap<>();
        for(AppModel appModel : appModels){
            String field1 = appModel.getField1();
            String groupInfo = appModel.getGroupInfo();
            if(field12groupInfo2appModels.containsKey(field1)){
                Map<String, List<AppModel>> groupInfo2appModels = field12groupInfo2appModels.get(field1);
                if(groupInfo2appModels.containsKey(groupInfo)){
                    groupInfo2appModels.get(groupInfo).add(appModel);
                } else {
                    List<AppModel> list = new ArrayList<>();
                    list.add(appModel);
                    groupInfo2appModels.put(groupInfo, list);
                }
            } else {
                Map<String, List<AppModel>> groupInfo2appModels = new LinkedHashMap<>();
                List<AppModel> list = new ArrayList<>();
                list.add(appModel);
                groupInfo2appModels.put(groupInfo, list);
                field12groupInfo2appModels.put(field1, groupInfo2appModels);
            }
        }
    }

}


package com.victor.utilities.report.excel.generator;

import com.victor.utilities.report.excel.generator.common.TabWriterBase;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * base class for sheet generation template
 */
public abstract class TabWriterBaseForAppData extends TabWriterBase {

    protected XSSFCellStyle headerStyleTemplateFontCenter;

    public TabWriterBaseForAppData(XSSFWorkbook wb) {
        super(wb);
    }

    @Override
    protected void initOwnCellStyles() {
        headerStyle = getCloneCellStyle(null);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(27, 145, 197)));
        XSSFFont headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setWrapText(true);

        subHeaderStyle = getCloneCellStyle(headerStyle);
        subHeaderStyle.setAlignment(CellStyle.ALIGN_LEFT);
        subHeaderStyle.setFont(boldFont);
        subHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(157, 222, 237)));

        subSubHeaderStyle = getCloneCellStyle(subHeaderStyle);
        subSubHeaderStyle.setAlignment(CellStyle.ALIGN_LEFT);
        subSubHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(150, 150, 150)));

        headerStyleTemplateFontCenter = getCloneCellStyle(subHeaderStyle);
        headerStyleTemplateFontCenter.setAlignment(CellStyle.ALIGN_CENTER);

        greenColor = new XSSFColor(new java.awt.Color(50, 196, 100));
        amberColor = new XSSFColor(new java.awt.Color(246, 197, 76));
        redColor = new XSSFColor(new java.awt.Color(243, 123, 83));

        greenStyle = getCloneCellStyle(null);
        greenStyle.setAlignment(CellStyle.ALIGN_CENTER);
        greenStyle.setFont(boldFont);
        greenStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        greenStyle.setFillForegroundColor(greenColor);
        amberStyle = getCloneCellStyle(greenStyle);
        amberStyle.setFillForegroundColor(amberColor);
        redStyle = getCloneCellStyle(greenStyle);
        redStyle.setFillForegroundColor(redColor);
    }

}


package com.victor.utilities.report.excel.generator;

import com.victor.utilities.report.excel.generator.common.ReportException;
import com.victor.utilities.report.excel.model.AppMetadata;
import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.util.AppSortingUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Generates Consolidated excel export for KRI MSPBNA
 */
public class TabWriterForAppData extends TabWriterBaseForAppData {

    private AppMetadata metadata;

    private Map<String, Map<String, List<AppModel>>> field12groupInfo2appModels;

    private boolean hasField1Header = false;

    public TabWriterForAppData(XSSFWorkbook wb, AppMetadata metadata) {
        super(wb);
        this.metadata = metadata;
    }

    @Override
    protected void generateSheetTab(Object data) throws ReportException {
        field12groupInfo2appModels = (Map<String, Map<String, List<AppModel>>>)data;
        doLayout();
        generateHeader();

        Row row, field1Row, groupInfoRow;
        for(Map.Entry<String, Map<String, List<AppModel>>> entry : field12groupInfo2appModels.entrySet()) {
            String field1 = entry.getKey();
            if(hasField1Header){
                sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 9));
                field1Row = sheet.createRow(rowIdx++);
                setCell(field1Row, 0, field1, subHeaderStyle);
            }
            Map<String, List<AppModel>> groupInfo2appModels = entry.getValue();
            for(Map.Entry<String, List<AppModel>> entry1 : groupInfo2appModels.entrySet()){
                String groupInfo = entry1.getKey();
                sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 9));
                groupInfoRow = sheet.createRow(rowIdx++);
                setCell(groupInfoRow, 0, groupInfo, hasField1Header ? subSubHeaderStyle : subHeaderStyle);

                List<AppModel> appModels = entry1.getValue();
                AppSortingUtil.sortByOrder(appModels);
                for(AppModel appModel : appModels){
                    row = sheet.createRow(rowIdx++);
                    colIdx = 0;
                    setCell(row, colIdx++, appModel.getOrder());
                    setCell(row, colIdx++, appModel.getDataHint(), stringDefaultStyle);
                    setCell(row, colIdx++, dateFormatter.format(dateKeyToRoundedDate(appModel.getCob())), stringDefaultStyle);
                    setCellLevel(row, colIdx++, appModel.getValueString(), appModel.getValue(), appModel);
                    setCell(row, colIdx++, appModel.getField3(), stringDefaultStyle);
                }
            }
        }
    }

    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private void generateHeader() {
        colIdx = 0;
        Row header1 = sheet.createRow(rowIdx++);
        header1.setHeight((short)900);
        setCell(header1, colIdx++, "Order", headerStyle);
        setCell(header1, colIdx++, "Data Hint", headerStyle);
        setCell(header1, colIdx++, "COB", headerStyle);
        setCell(header1, 4, "Value", headerStyle) ;
        setCell(header1, 5, "Field2", headerStyle);

        Row header2 = sheet.createRow(rowIdx++);
        setCell(header2, 2, "Top", amberStyle);
        setCell(header2, 3, "Bottom", redStyle);
    }

    private void doLayout() {
        int startRow = 0;
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+0, 2, 3));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+1, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+1, 5, 5));
        sheet.createFreezePane(1, startRow+2, 1, startRow+2);
    }

    public static Date dateKeyToRoundedDate(Integer dateKeyInteger) {
        Date cob = null;
        if (dateKeyInteger != null && dateKeyInteger != 0) {
            int dateKey = dateKeyInteger;
            int year = dateKey / 10000;
            int month = dateKey % 10000 / 100;
            int day = dateKey % 100;
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cob = cal.getTime();
        }
        return cob;
    }
}


-- vi/vim
:q!                         -- quit without save
:qw                         -- quit with save
ZZ                          -- quit with save
:w filename2                -- save to another file filename2

Cursor Move
0 or |	                    Positions cursor at beginning of line.
$	                        Positions cursor at end of line.
w	                        Positions cursor to the next word.
b	                        Positions cursor to previous word.
(	                        Positions cursor to beginning of current sentence.
)	                        Positions cursor to beginning of next sentence.
E	                        Move to the end of Blank delimited word
{	                        Move a paragraph back
}	                        Move a paragraph forward
[[	                        Move a section back
]]	                        Move a section forward
n|	                        Moves to the column n in the current line
1G	                        Move to the first line of the file
G	                        Move to the last line of the file
nG	                        Move to nth line of the file
:n	                        Move to nth line of the file
H	                        Move to top of screen
nH	                        Moves to nth line from the top of the screen
M	                        Move to middle of screen
L	                        Move to botton of screen
nL	                        Moves to nth line from the bottom of the screen
:n	                        Colon followed by a number would position the cursor on line number represented by n

Control Commands
CTRL+d	                    Move forward 1/2 screen
CTRL+f	                    Move forward one full screen
CTRL+u	                    Move backward 1/2 screen
CTRL+b	                    Move backward one full screen
CTRL+e	                    Moves screen up one line
CTRL+y	                    Moves screen down one line

Editing Files
i	                        Inserts text before current cursor location.
I	                        Inserts text at beginning of current line.
a	                        Inserts text after current cursor location.
A	                        Inserts text at end of current line.
o	                        Creates a new line for text entry below cursor location.
O	                        Creates a new line for text entry above cursor location.

Deleting Characters
x	                        Deletes the character under the cursor location.
X	                        Deletes the character before the cursor location.
dw	                        Deletes from the current cursor location to the next word.
d^	                        Deletes from current cursor position to the beginning of the line.
d$	                        Deletes from current cursor position to the end of the line.
D	                        Deletes from the cursor position to the end of the current line.
dd	                        Deletes the line the cursor is on.

Change Commands
cc	                        Removes contents of the line, leaving you in insert mode.
cw	                        Changes the word the cursor is on from the cursor to the end of the word.
r	                        Replaces the character under the cursor. vi returns to command mode after the replacement is entered.
R	                        Overwrites multiple characters beginning with the character currently under the cursor. You must use Esc to stop the overwriting.
s	                        Replaces the current character with the character you type. Afterward, you are left in insert mode.
S	                        Deletes the line the cursor is on and replaces with new text. After the new text is entered, vi remains in insert mode.

Copy and Past Commands
yy	                        Copies the current line.
yw	                        Copies the current word from the character the cursor is on until the end of the word.
p	                        Puts the copied text after the cursor.
P	                        Puts the yanked text before the cursor.

Advanced Commands
J	                        Join the current line with the next one. A count joins that many lines.
<<	                        Shifts the current line to the left by one shift width.
>>	                        Shifts the current line to the right by one shift width.
~	                        Switch the case of the character under the cursor.
^G	                        Press CNTRL and G keys at the same time to show the current filename and the status.
U	                        Restore the current line to the state it was in before the cursor entered the line.
u	                        Undo the last change to the file. Typing 'u' again will re-do the change.
:f	                        Displays current position in the file in % and file name, total number of file.
:f filename	                Renames current file to filename.
:w filename	                Write to file filename.
:e filename	                Opens another file with filename.
:cd dirname	                Changes current working directory to dirname.
:e #	                    Use to toggle between two opened files.
:n	                        In case you open multiple files using vi, use :n to go to next file in the series.
:p	                        In case you open multiple files using vi, use :p to go to previous file in the series.
:N	                        In case you open multiple files using vi, use :N to go to previous file in the series.
:r file	                    Reads file and inserts it after current line
:nr file	                Reads file and inserts it after line n.

Word and Character Searching
/                           searches forwards (downwards) in the file.
?                           searches backwards (upwards) in the file.
f                           search forwards for a character on the current line only
F                           search backwards for a character on the current line only
t                           search for a character on the current line only, for t, the cursor moves to the position before the character
T                           search for a character on the current line only, for t, the cursor moves to the position after the character
special character
^	                        Search at the beginning of the line. (Use at the beginning of a search expression.)
$	                        End of the line (Use at the end of the search expression.)
.	                        Matches a single character.
*	                        Matches zero or more of the previous character.
[	                        Starts a set of matching, or non-matching expressions.
<	                        Put in an expression escaped with the backslash to find the ending or beginning of a word.
>	                        See the '<' character description above.

Replacing Text
:s/search/replace/g         substitute search with replace

Set Commands
:set ic	                    Ignores case when searching
:set ai	                    Sets autoindent
:set noai	                To unset autoindent.
:set nu	                    Displays lines with line numbers on the left side.
:set sw	                    Sets the width of a software tabstop. For example you would set a shift width of 4 with this command: :set sw=4
:set ws	                    If wrapscan is set, if the word is not found at the bottom of the file, it will try to search for it at the beginning.
:set wm	                    If this option has a value greater than zero, the editor will automatically "word wrap". For example, to set the wrap margin to two characters, you would type this: :set wm=2
:set ro	                    Changes file type to "read only"
:set term	                Prints terminal type
:set bf	                    Discards control characters from input



-- grep                     line based
-v	                        Print all lines that do not match pattern.
-n	                        Print the matched line and its line number.
-l	                        Print only the names of files with matching lines (letter "l")
-c	                        Print only the count of matching lines.
-i	                        Match either upper- or lowercase.
ls -l | grep "Aug"

-- sort                     line based
-n	                        Sort numerically (example: 10 will sort after 2), ignore blanks and tabs.
-r	                        Reverse the order of sort.
-f	                        Sort upper- and lowercase together.
-k n                        sorting key is nth column.
ls -l | grep "Aug" | sort +4n


-- System Performance
--------------------- CPU  --------------------------------------
--------------------- Memory ------------------------------------
--------------------- Disk space --------------------------------
--------------------- Communications lines ----------------------
--------------------- I/O Time ----------------------------------
--------------------- Network Time ------------------------------
--------------------- Applications programs ---------------------
nice/renice	                Run a program with modified scheduling priority             nice -12 large-job
netstat	                    Print network connections, routing tables, interface statistics, masquerade connections, and multicast memberships
time	                    Time a simple command or give resource usage
uptime	                    System Load Average
ps	                        Report a snapshot of the current processes.
vmstat	                    Report virtual memory statistics
gprof	                    Display call graph profile data
prof	                    Process Profiling
top	                        Display system tasks

-- file
ls -l                       -- file type and permission / number of memory blocks / owner / group / file size / last modified time / name
ls -a                       -- show hidden
ls *.doc
file filename	            Identifies the file type (binary, text, etc).
find filename/dir	        Finds a file/directory.
head filename	            Shows the beginning of a file.
tail filename	            Shows the end of a file.
less filename	            Browses through a file from end to beginning.
more filename	            Browses through a file from beginning to end.
touch filename	            Creates a blank file or modifies an existing file.s attributes.
whereis filename	        Shows the location of a file.
which filename	            Shows the location of a file if it is in your PATH.
cp old_file new_file
mv old_file new_file		-- rename
rm filename
cat filename
cat -b filename				-- with line number
wc filename1 filename2		-- total number of lines / total number of words / total number of bytes / file name

-- directory
cd ~                        -- go to home directory
cd ~username                -- go to other user's home directory
cd -                        -- go to last directory
pwd                         -- current working directory
mkdir dir_name
mkdir -p /a/b/test          -- creates all the necessary directories even not exist yet
rmdir dirname
mv old_dir new_dir		    -- rename
df -h                       displays the disk space usage in kilobytes
du -h /etc                  show disk space usage on a particular directory
mount                       what is currently mounted on your system

-- environment              /etc/profile and ~/.profile
TEST="Unix Programming"
echo $TEST
PS1="[\u@\h \w]\$"          -- command prompt
PS2="->"                    -- secondary command prompt
$HOME $IFS $LANG $PATH $PWD $RANDOM $SHLVL $TZ $UID $TERM

-- process
ps -f                       -- listing running processes
ls ch*.doc &                -- background process
kill pid
kill -9 pid
top                         -- showing processes sorted by various criteria (physical and virtual memory, CPU usage, load averages, busy processes)

-- network
ping hostname or ip
telnet hostname or ip
finger                      -- displays information about users on a given host
ftp hostname or ip
put filename	            Upload filename from local machine to remote machine.
get filename	            Download filename from remote machine to local machine.
mput file list	            Upload more than one files from local machine to remote machine.
mget file list	            Download more than one files from remote machine to local machine.
prompt off	                Turns prompt off, by default you would be prompted to upload or download movies using mput or mget commands.
prompt on	                Turns prompt on.
dir	                        List all the files available in the current directory of remote machine.
cd dirname	                Change directory to dirname on remote machine.
lcd dirname	                Change directory to dirname on local machine.
quit	                    Logout from the current login.

-- user & group
whoami
user
who
w
/etc/passwd                 Keeps user account and password information. This file holds the majority of information about accounts on the Unix system.
/etc/shadow                 Holds the encrypted password of the corresponding account. Not all the system support this file.
/etc/group                  This file contains the group information for each account.
/etc/gshadow                This file contains secure group account information.

-- file system
/	                        This is the root directory which should contain only the directories needed at the top level of the file structure.
/bin	                    This is where the executable files are located. They are available to all user.
/dev	                    These are device drivers.
/etc	                    Supervisor directory commands, configuration files, disk configuration files, valid user lists, groups, ethernet, hosts, where to send critical messages.
/lib	                    Contains shared library files and sometimes other kernel-related files.
/boot	                    Contains files for booting the system.
/home	                    Contains the home directory for users and other accounts.
/mnt	                    Used to mount other temporary file systems, such as cdrom and floppy for the CD-ROM drive and floppy diskette drive, respectively
/proc	                    Contains all processes marked as a file by process number or other information that is dynamic to the system.
/tmp	                    Holds temporary files used between system boots
/usr	                    Used for miscellaneous purposes, or can be used by many users. Includes administrative commands, shared files, library files, and others
/var	                    Typically contains variable-length files such as log and print files and any other type of file that may contain a variable amount of data
/sbin	                    Contains binary (executable) files, usually for system administration. For example fdisk and ifconfig utlities.
/kernel	                    Contains kernel files

