package com.victor.utilities.report.excel.parser.common;


/**
 * represent tab row data
 */
public class TabRow {

    private TabCell cells[] = new TabCell[10];

    private int maxColumnIndex = -1;

    private int rowIdx;

    private int dataCount;

    public TabRow(int rowIdx) {
        this.rowIdx = rowIdx;
    }

    public void addCell(int rowIdx, int columnIdx, String content){
        if(this.rowIdx != rowIdx){
            throw new IllegalArgumentException("row index is not match");
        }
        maxColumnIndex = Math.max(maxColumnIndex, columnIdx);
        rangeCheckAndExpand(columnIdx);
        if(cells[columnIdx] == null){
            ++dataCount;
        }
        cells[columnIdx] = new TabCell(rowIdx, columnIdx, content);
    }

    public void addCell(int columnIdx, String content){
        addCell(rowIdx, columnIdx, content);
    }

    private void rangeCheckAndExpand(int index){
        if(index >= cells.length){
            TabCell newCells[] = new TabCell[index + 1];
            for (int i = 0; i < cells.length; ++i){
                newCells[i] = cells[i];
            }
            cells = newCells;
        }
    }

    public boolean containsData(){
        return dataCount > 0;
    }

    public boolean containsData(int index){
        if(index > maxColumnIndex){
            return false;
        } else {
            return cells[index] != null;
        }
    }

    public TabCell getCell(int columnIndex) {
        if(columnIndex < 0 || columnIndex > maxColumnIndex){
            return null;
        } else {
            return cells[columnIndex];
        }
    }

    public TabCell getCell(String columnIndex) {
        return getCell(getColNum(columnIndex));
    }

    public String getCellData(int columnIndex) {
        TabCell cell = getCell(columnIndex);
        return cell != null ? cell.getContent() : null;
    }

    public String getCellData(String columnIndex) {
        return getCellData(getColNum(columnIndex));
    }

    /**
     * convert alphabetic column to int
     * @param colName
     * @return
     */
    public int getColNum(String colName) {
        //remove any whitespace
        colName = colName.trim();
        StringBuffer buff = new StringBuffer(colName);
        //string to lower case, reverse then place in char array
        char chars[] = buff.reverse().toString().toLowerCase().toCharArray();
        int retVal=0, multiplier=0;
        for(int i = 0; i < chars.length;i++){
            //retrieve ascii value of character, subtract 96 so number corresponds to place in alphabet. ascii 'a' = 97
            multiplier = (int)chars[i]-96;
            //mult the number by 26^(position in array)
            retVal += multiplier * Math.pow(26, i);
        }
        return retVal;
    }

    public int getDataCount() {
        return dataCount;
    }

    public int getMaxColumnIndex() {
        return maxColumnIndex;
    }
}