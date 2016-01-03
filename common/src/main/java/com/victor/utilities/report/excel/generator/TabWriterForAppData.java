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