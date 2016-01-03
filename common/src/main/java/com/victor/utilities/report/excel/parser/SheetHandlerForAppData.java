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