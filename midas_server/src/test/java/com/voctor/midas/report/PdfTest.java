package com.voctor.midas.report;


import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfTest {

    @Ignore
    @Test
    public void testSplit() throws IOException {
        String filePath = "F:/test.pdf";
        File file = new File(filePath);
        PDDocument document = PDDocument.load(file);
        Splitter splitter = new Splitter();
        int split = 2;
        splitter.setSplitAtPage( document.getNumberOfPages() / split + 1 );
        List<PDDocument> documents = splitter.split( document );
        for( int i=0; i<documents.size(); i++ )
        {
            PDDocument doc = documents.get( i );
            String fileName = filePath.substring(0, filePath.length() - 4) + "_" + i + ".pdf";
            doc.save(fileName);
            doc.close();
        }
    }

    @Ignore
    @Test
    public void testMerge() throws IOException {
        String filePath1 = "F:/test1.pdf";
        String filePath2 = "F:/test2.pdf";
        String destFilePath = "F:/merged.pdf";
        destFilePath = filePath1.substring(0, filePath1.length() - 4) + "_merged.pdf";

        File file1 = new File(filePath1);
        PDDocument doc1 = PDDocument.load(file1);

        File file2 = new File(filePath2);
        PDDocument doc2 = PDDocument.load(file2);

        PDFMergerUtility pdfMergeUtility = new PDFMergerUtility();
        pdfMergeUtility.setDestinationFileName(destFilePath);

        pdfMergeUtility.addSource(file1);
        pdfMergeUtility.addSource(file2);

        MemoryUsageSetting memUsageSetting = MemoryUsageSetting.setupMainMemoryOnly(1024 * 1024 * 512);
        pdfMergeUtility.mergeDocuments(memUsageSetting);

        System.out.println("Documents merged");

        doc1.close();
        doc2.close();
    }

}
