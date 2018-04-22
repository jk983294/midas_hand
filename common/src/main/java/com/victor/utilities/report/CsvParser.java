package com.victor.utilities.report;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvParser {

    private static Logger logger = Logger.getLogger(CsvParser.class);

    public Map<String, List<String>> columns = new HashMap<>();

    public LinkedList<List<String>> rows = new LinkedList<>();

    public List<String> header = new ArrayList<>();

    public int columnCount;

    public boolean hasHeader = true;

    public String delimiter = "\t";

    public Map<String, Integer> column2index = new HashMap<>();

    public Map<String, String> metadata = new HashMap<>();  // key value of metadata

    public CsvParser() {
    }

    public void parse(String filePath) throws IOException {
        clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                line = line.replace("\\n", "\n");
                if(StringUtils.isNotEmpty(line)){
                    String[] lets = line.split(delimiter, -1);
                    rows.add(new ArrayList<>(Arrays.asList(lets)));
                }
            }
        } catch (IOException e) {
            logger.error("parse csv failed", e);
            throw e;
        }

        if(rows.size() == 0) return;

        if(hasHeader){
            header = rows.removeFirst();
            columnCount = header.size();
        } else {
            columnCount = rows.getFirst().size();
            for(int i = 0; i < columnCount; ++i){
                header.add("header" + i);
            }
        }

        List<List<String>> columnArray = new ArrayList<>();
        for(int i = 0; i < columnCount; ++i){
            columnArray.add(new ArrayList<>());
        }


        int cnt = 0;
        for(List<String> row : rows){
            if(row.size() == columnCount){
                for(int i = 0; i < columnCount; ++i){
                    columnArray.get(i).add(row.get(i));
                }
            } else if(row.size() == columnCount - 1){
                for(int i = 0; i < columnCount - 1; ++i){
                    columnArray.get(i).add(row.get(i));
                }
                // last value can be null, like a,b,c,
                columnArray.get(columnCount - 1).add("null");
            } else {
                throw new RuntimeException(filePath + " : data count not correct in row " + cnt);
            }
            cnt++;
        }

        int missingCount = 0;
        for(int i = 0; i < columnCount; ++i){
            String columnName = header.get(i);
            if(StringUtils.isEmpty(columnName)){
                columnName = "default" + missingCount;
                missingCount++;
            }
            columns.put(columnName, columnArray.get(i));
            column2index.put(columnName, i);
        }
    }

    public void clear(){
        columns.clear();
        rows.clear();
        header.clear();
        column2index.clear();
        metadata.clear();
        columnCount = 0;
    }

    public String getRowAt(List<String> row, int columnIndex){
        return row.get(columnIndex);
    }

    public String getRowAt(List<String> row, String column){
        return row.get(column2index.get(column));
    }

    public void mergeDateTime(){
        if(header.size() >= 2 && header.get(0).equalsIgnoreCase("date") && header.get(1).equalsIgnoreCase("time")){
            String dateColumnName = header.get(0);
            String timeColumnName = header.get(1);

            List<String> dateColumn = columns.get(dateColumnName);
            List<String> timeColumn = columns.get(timeColumnName);
            int i = 0;
            for(List<String> l : rows){
                String merged = dateColumn.get(i) + " " + timeColumn.get(i);
                timeColumn.set(i, merged);
                l.set(1, merged);
                l.remove(0);
                ++i;
            }

            header.remove(0);
            column2index.remove(dateColumnName);
            columns.remove(dateColumnName);
            for(String key : column2index.keySet()){
                column2index.put(key, column2index.get(key) - 1);
            }
        }
    }

}
