package com.victor.utilities.report;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvParser {

    private static Logger logger = Logger.getLogger(CsvParser.class);

    public String filePath;

    public Map<String, List<String>> columns = new HashMap<>();

    public LinkedList<List<String>> rows = new LinkedList<>();

    public List<String> header = new ArrayList<>();

    public int dataCount;

    public boolean hasHeader = true;

    public String delimiter = "\t";

    public CsvParser(String filePath) {
        this.filePath = filePath;
    }

    public CsvParser(String filePath, boolean hasHeader) {
        this.filePath = filePath;
        this.hasHeader = hasHeader;
    }

    public void parse(){
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                line = line.replace("\\n", "\n");
                if(StringUtils.isNotEmpty(line)){
                    String[] lets = line.split(delimiter);
                    rows.add(new ArrayList<>(Arrays.asList(lets)));
                }
            }
        } catch (IOException e) {
            logger.error("parse csv failed", e);
        }

        if(rows.size() == 0) return;

        if(hasHeader){
            header = rows.removeFirst();
            dataCount = header.size();
        } else {
            dataCount = rows.getFirst().size();
            for(int i = 0; i < dataCount; ++i){
                header.add("header" + i);
            }
        }

        List<List<String>> columnArray = new ArrayList<>();
        for(int i = 0; i < dataCount; ++i){
            columnArray.add(new ArrayList<String>());
        }


        int cnt = 0;
        for(List<String> row : rows){
            if(row.size() == dataCount){
                for(int i = 0; i < dataCount; ++i){
                    columnArray.get(i).add(row.get(i));
                }
            } else {
                throw new RuntimeException("data count not correct in row " + cnt);
            }
            cnt++;
        }

        for(int i = 0; i < dataCount; ++i){
            columns.put(header.get(i), columnArray.get(i));
        }
    }

}
