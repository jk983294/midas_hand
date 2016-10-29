package com.victor.midas.report;


import com.victor.midas.model.report.ReportLine;
import com.victor.utilities.utils.RegExpHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReportUtils {

    public static final Pattern datePattern = Pattern.compile("\\d{4}年(\\s)?\\d{1,2}月(\\s)?\\d{1,2}日");

    public static boolean isDate(String str){
        return str != null && datePattern.matcher(str).matches();
    }


    public static List<ReportLine> query(List<ReportLine> lines, String toSearch){
        List<ReportLine> results = new ArrayList<>();
        for(ReportLine line : lines){
            if(line.text.contains(toSearch)){
                results.add(line);
            }
        }
        return results;
    }

    public static List<ReportLine> query(List<ReportLine> lines, Pattern toSearch){
        List<ReportLine> results = new ArrayList<>();
        for(ReportLine line : lines){
            if(RegExpHelper.isMatch(line.text, toSearch)){
                results.add(line);
            }
        }
        return results;
    }

    public static String getStockCodeFromFilePath(String path){
        int idx = path.indexOf("cninfo");
        return path.substring(idx + 7, idx + 13);
    }

}
