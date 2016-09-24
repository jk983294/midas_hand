package com.voctor.midas.report;


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
            String fileName = filePath.substring(0, filePath.length()-4 ) + "_" + i + ".pdf";
            doc.save(fileName);
            doc.close();
        }
    }

}
