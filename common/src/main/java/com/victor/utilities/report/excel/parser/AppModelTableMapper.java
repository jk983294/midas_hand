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