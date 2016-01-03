package com.victor.utilities.report.excel.model;

public class AppModel {

    private String field1, groupInfo, valueString, dataHint;

    private Integer order, cob;

    private Double field3, value;

    private ColorTag colorTag;

    private int decimal;
    private NumberFormat numberFormat;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getCob() {
        return cob;
    }

    public void setCob(Integer cob) {
        this.cob = cob;
    }

    public void setField3(Double field3) {
        this.field3 = field3;
    }

    public double getField3() {
        return field3;
    }

    public void setField3(double field3) {
        this.field3 = field3;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public ColorTag getColorTag() {
        return colorTag;
    }

    public void setColorTag(ColorTag colorTag) {
        this.colorTag = colorTag;
    }

    public String getDataHint() {
        return dataHint;
    }

    public void setDataHint(String dataHint) {
        this.dataHint = dataHint;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public AppModel() {

    }
}