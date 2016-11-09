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
        String[] files = {
                "F:/test1.pdf",
                "F:/test2.pdf"
        };
        String destFilePath = "F:/merged.pdf";

        PDDocument[] docs = new PDDocument[files.length];
        for (int i = 0; i < files.length; i++) {
            docs[i] = PDDocument.load(new File(files[i]));
        }

        PDFMergerUtility pdfMergeUtility = new PDFMergerUtility();
        pdfMergeUtility.setDestinationFileName(destFilePath);

        for (int i = 0; i < files.length; i++) {
            pdfMergeUtility.addSource(new File(files[i]));
        }

        MemoryUsageSetting memUsageSetting = MemoryUsageSetting.setupMainMemoryOnly(1024 * 1024 * 1024);
        pdfMergeUtility.mergeDocuments(memUsageSetting);

        System.out.println("Documents merged");
    }

}
