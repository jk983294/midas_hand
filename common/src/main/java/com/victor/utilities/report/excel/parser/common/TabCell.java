package com.victor.utilities.report.excel.parser.common;

/**
 * represent tab cell data
 */
public class TabCell {

    private int rowIdx, columnIdx;

    private String content;

    public TabCell(int rowIdx, int columnIdx, String content) {
        this.rowIdx = rowIdx;
        this.columnIdx = columnIdx;
        this.content = content;
    }

    public TabCell() {
    }

    public int getRowIdx() {
        return rowIdx;
    }

    public void setRowIdx(int rowIdx) {
        this.rowIdx = rowIdx;
    }

    public int getColumnIdx() {
        return columnIdx;
    }

    public void setColumnIdx(int columnIdx) {
        this.columnIdx = columnIdx;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}