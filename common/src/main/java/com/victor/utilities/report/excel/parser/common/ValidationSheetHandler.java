package com.victor.utilities.report.excel.parser.common;

import com.victor.utilities.report.excel.parser.common.SheetBaseHandler;
import com.victor.utilities.report.excel.model.AppMetadata;
import org.apache.commons.lang3.StringUtils;
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