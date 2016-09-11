package com.victor.midas.report;


import com.victor.midas.model.report.ReportLine;
import com.victor.midas.model.report.ReportTable;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BalanceSheetExtractor {

    private static final Logger logger = Logger.getLogger(BalanceSheetExtractor.class);

    public static final Pattern balanceSheetHeaderPattern = Pattern.compile("^(合并|母公司)?资(\\s)?产(\\s)?负(\\s)?债(\\s)?表$");
    public static final Pattern balanceSheetTableEndPattern = Pattern.compile("^负债及股东权益总计.*");

    public ArrayList<ReportLine> lines;
    public List<ReportTable> tables;
    public boolean isAnnotationUseNumber = false;

    public void init(ArrayList<ReportLine> lines){
        this.lines = lines;
        tables = new ArrayList<>();
    }

    public void extract(){
        tableBoundary();

        if(CollectionUtils.isEmpty(tables)){
            logger.warn("no balance sheet found.");
        } else if(tables.size() > 2){
            logger.warn("more than two balance sheets found.");
        }

        for (ReportTable table : tables){
            extractTable(table.contents);
        }
    }

    private void extractTable(List<ReportLine> contents){
        if(contents == null || contents.size() < 5){
            logger.warn("no content found in balance sheet.");
            return;
        }

        checkAnnotationUseNumber(contents);

        String content;
        Double d1, d2;
        int numberCount = 0;
        for (int i = 4; i < contents.size(); i++) {
            content = contents.get(i).text;
            String[] lets = null;
            if(content.contains("\t")){
                lets = content.split("\t");
            } else if(content.contains(" ")){
                lets = content.split(" ");
            }

            if(lets == null || lets.length < 2) continue;

            d1 = RegExpHelper.getNumber(lets[lets.length - 2]);     // previous last value
            d2 = RegExpHelper.getNumber(lets[lets.length - 1]);     // last value
            numberCount = RegExpHelper.numberCount(content);


            if(d2 == null){ // no value found
            } else if(d1 == null){  // one value found
                if(isAnnotationUseNumber && d2 < 100 && RegExpHelper.isInt(lets[lets.length - 1])){
                    // the only value found is annotation
                } else {
                    // hard to determine this value is current year or last year data
                }
            } else {    // two value found
                if(isAnnotationUseNumber && d1 < 100 && RegExpHelper.isInt(lets[lets.length - 2])){
                    // the previous last value found is annotation
                } else {

                }
            }



        }
    }

    private void checkAnnotationUseNumber(List<ReportLine> contents){
        for (int i = 4; i < contents.size(); i++) {
            String content = contents.get(i).text;
            if(RegExpHelper.numberCount(content) == 3 && !RegExpHelper.contains(content, ReportUtils.datePattern)){
                isAnnotationUseNumber = true;
                return;
            }
        }
        isAnnotationUseNumber = false;
    }

    private void tableBoundary(){
        List<ReportLine> headers = ReportUtils.query(lines, balanceSheetHeaderPattern);
        List<ReportLine> foots = ReportUtils.query(lines, balanceSheetTableEndPattern);
        if(CollectionUtils.isNotEmpty(headers) && CollectionUtils.isNotEmpty(foots)){
            int headerIndex = 0, footIndex = 0;
            while (headerIndex < headers.size()){
                ReportLine line = headers.get(headerIndex);
                if(line.lineNumber + 1 < lines.size() && ReportUtils.isDate(lines.get(line.lineNumber + 1).text)){
                    while (footIndex < foots.size()){
                        ReportLine foot = foots.get(footIndex);
                        if(foot.lineNumber > line.lineNumber && lines.get(foot.lineNumber + 1).text.startsWith("法定代表人")){
                            break;
                        }
                        footIndex++;
                    }
                    if(footIndex < foots.size()){
                        tables.add(new ReportTable(lines.subList(headers.get(headerIndex).lineNumber,
                                foots.get(footIndex).lineNumber + 1)));
                        footIndex++;
                    }
                }
                headerIndex++;
            }
        }
    }
}
