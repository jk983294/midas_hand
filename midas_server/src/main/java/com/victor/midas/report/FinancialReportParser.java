package com.victor.midas.report;

import com.victor.midas.model.report.MidasReportData;
import com.victor.midas.model.report.ReportLine;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * parse financial reports
 */
public class FinancialReportParser {

    private static final Logger logger = Logger.getLogger(FinancialReportParser.class);

    public String filePath;
    public MidasReportData reportData;
    public ArrayList<ReportLine> lines;
    public BalanceSheetExtractor balanceSheetExtractor = new BalanceSheetExtractor();


    public void init(String filePath){
        this.filePath = filePath;
    }

    public void parse() throws IOException {
        String fileName = FilenameUtils.removeExtension(FilenameUtils.getName(filePath));
        String[] attrs = fileName.split("_");
        reportData = new MidasReportData(attrs[0], attrs[2], Integer.valueOf(attrs[1]));

        File file = new File(filePath);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();

        if(!text2lines(pdfStripper.getText(document))) return;

        balanceSheetExtractor.init(lines);
        balanceSheetExtractor.extract();
    }

    private boolean text2lines(String text){
        if(StringUtils.isEmpty(text)) return false;
        text = text.replace("\r", "");

        String[] texts = text.split("\n");
        lines = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < texts.length; i++) {
            String data = RegExpHelper.condense(texts[i].trim());
            if(ReportLine.isEmptyLine(data)){
                continue;
            }
            lines.add(new ReportLine(count++, i, data));
        }
        return CollectionUtils.isNotEmpty(lines);
    }


}
