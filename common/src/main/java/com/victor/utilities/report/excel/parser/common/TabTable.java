package com.victor.utilities.report.excel.parser.common;


import org.apache.commons.lang3.StringUtils;

/**
 * represent tab table data
 */
public class TabTable {

    private TabRow rows[] = new TabRow[60];

    private int maxRowIndex = -1, maxColumnIndex = -1;

    private int rowCount;

    public TabTable() {
    }

    public void addCell(int rowIdx, int columnIdx, String content){
        if(StringUtils.isEmpty(content)){
            return;
        }
        maxRowIndex = Math.max(maxRowIndex, rowIdx);
        maxColumnIndex = Math.max(maxColumnIndex, rowIdx);
        rangeCheckAndExpand(rowIdx);
        if(rows[rowIdx] == null){
            ++rowCount;
            rows[rowIdx] = new TabRow(rowIdx);
        }
        rows[rowIdx].addCell(rowIdx, columnIdx, content);
    }

    private void rangeCheckAndExpand(int index){
        if(index >= rows.length){
            TabRow newRows[] = new TabRow[index + 1];
            for (int i = 0; i < rows.length; ++i){
                newRows[i] = rows[i];
            }
            rows = newRows;
        }
    }

    public boolean containsRow(){
        return rowCount > 0;
    }

    public boolean containsRow(int index){
        if(index > maxRowIndex){
            return false;
        } else {
            return rows[index] != null;
        }
    }

    public TabRow getRow(int rowIndex){
        if(rowIndex < 0 || rowIndex > maxRowIndex){
            return null;
        } else {
            return rows[rowIndex];
        }
    }

    public TabCell getCell(int rowIndex, int columnIndex){
        TabRow row = getRow(rowIndex);
        if(row != null){
            return row.getCell(columnIndex);
        }
        return null;
    }

    public String getCellData(int rowIndex, int columnIndex){
        TabCell cell = getCell(rowIndex, columnIndex);
        if(cell != null){
            return cell.getContent();
        }
        return null;
    }

    public TabCell getCell(int rowIndex, String columnIndex){
        return getCell(rowIndex, getColNum(columnIndex));
    }

    public String getCellData(int rowIndex, String columnIndex){
        return getCellData(rowIndex, getColNum(columnIndex));
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

    public int getMaxRowIndex() {
        return maxRowIndex;
    }

    public int getMaxColumnIndex() {
        return maxColumnIndex;
    }
}