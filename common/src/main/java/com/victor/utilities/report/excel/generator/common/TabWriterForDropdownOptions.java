package com.victor.utilities.report.excel.generator.common;

import com.victor.utilities.report.excel.generator.common.ReportException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * options sheet generation template
 */
public class TabWriterForDropdownOptions extends TabWriterBase {

    public TabWriterForDropdownOptions(XSSFWorkbook wb) {
        super(wb);
    }

    @Override
    protected void generateSheetTab(Object data) throws ReportException {
        LinkedHashMap<String, String[]> title2options = (LinkedHashMap<String, String[]>)data;
        List<String> titles = new ArrayList<>();
        int maxOptionCnt = 0;
        for(Map.Entry<String, String[]> entry : title2options.entrySet()) {
            titles.add(entry.getKey());
            maxOptionCnt = Math.max(maxOptionCnt, entry.getValue().length);
        }
        Row row, headerRow;
        headerRow = sheet.createRow(rowIdx++);
        for (int i = 0; i < titles.size(); i++){
            setCell(headerRow, colIdx++, titles.get(i), stringDefaultStyle);
        }
        for (int i = 0; i < maxOptionCnt; i++){
            row = sheet.createRow(rowIdx++);
            colIdx = 0;
            for(Map.Entry<String, String[]> entry : title2options.entrySet()) {
                String[] options = entry.getValue();
                if(options.length > i){
                    setCell(row, colIdx, options[i], stringDefaultStyle);
                }
                colIdx++;
            }
        }
    }

    @Override
    protected void initOwnCellStyles() {
        XSSFColor borderColorGrey = new XSSFColor(new java.awt.Color(217, 217, 217));
        defaultStyle.setBottomBorderColor(borderColorGrey);
        defaultStyle.setTopBorderColor(borderColorGrey);
        defaultStyle.setLeftBorderColor(borderColorGrey);
        defaultStyle.setRightBorderColor(borderColorGrey);
        defaultStyle.setBorderLeft(BorderStyle.NONE);
        defaultStyle.setBorderRight(BorderStyle.NONE);
        defaultStyle.setBorderTop(BorderStyle.DOTTED);
        defaultStyle.setBorderBottom(BorderStyle.DOTTED);

        greenColor = new XSSFColor(new java.awt.Color(146, 208, 80));
        amberColor = new XSSFColor(new java.awt.Color(255, 192, 0));
        redColor = new XSSFColor(new java.awt.Color(255, 0, 0));
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
