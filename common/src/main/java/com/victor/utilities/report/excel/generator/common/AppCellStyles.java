package com.victor.utilities.report.excel.generator.common;

import com.victor.utilities.report.excel.model.ColorTag;
import com.victor.utilities.report.excel.model.NumberFormat;
import com.victor.utilities.report.excel.model.NumberMagnitude;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

/**
 * contains cell styles for one type
 */
public class AppCellStyles {

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

    public AppCellStyles(int decimal, XSSFWorkbook wb, XSSFColor hintColor, XSSFColor greenColor, XSSFColor amberColor, XSSFColor redColor, XSSFCellStyle defaultStyle) {
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