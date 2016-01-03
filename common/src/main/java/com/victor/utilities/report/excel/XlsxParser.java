package com.victor.utilities.report.excel;

import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.parser.SheetHandlerForAppData;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XlsxParser {

    private static Logger logger = LoggerFactory.getLogger(XlsxParser.class);

    /**
     * parse AppData excel file
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static List<AppModel> parseExceptionRules(InputStream inputStream) throws Exception {
        logger.info("Parse excel start...");
        List<AppModel> data = new ArrayList<AppModel>();
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");

        SheetHandlerForAppData handler = new SheetHandlerForAppData(sst);
        parser.setContentHandler((ContentHandler)handler);

        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) r.getSheetsData();
        if(sheets.hasNext()){
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            return handler.getAppData();
        }
        return data;
    }
}