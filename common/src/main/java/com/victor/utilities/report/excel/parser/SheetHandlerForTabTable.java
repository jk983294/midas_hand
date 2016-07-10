package com.victor.utilities.report.excel.parser;

import com.victor.utilities.report.excel.parser.common.TabTable;
import com.victor.utilities.report.excel.parser.common.ValidationSheetHandler;
import org.apache.poi.xssf.model.SharedStringsTable;

import java.util.HashSet;
import java.util.Set;

/**
 * extract exception rules
 */
public class SheetHandlerForTabTable extends ValidationSheetHandler {

    public SheetHandlerForTabTable(SharedStringsTable sst) {
        super(sst, null);
    }

    /**
     * extract data in Excel "Data" table
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

    public TabTable getTabTable() {
        return tabTable;
    }
}