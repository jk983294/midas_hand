package com.victor.midas.services.worker.task.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.victor.midas.model.vo.StockReport;
import com.victor.midas.report.ReportUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ReportIndexer {

    private static final Logger logger = Logger.getLogger(ReportIndexer.class);

    private IndexWriter writer;
    private FileFilter filter = new ReportFileFilter();
    private Map<String, StockReport> code2reports;
    public Map<String, StockReport> stockIndexed = new HashMap<>();

    int fileCount = 0;

    public ReportIndexer(String indexDirectoryPath, Map<String, StockReport> code2reports) throws IOException {
        this.code2reports = code2reports;
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setRAMBufferSizeMB(128.0).setCommitOnClose(true);
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws IOException{
        writer.close();
    }

    /**
     * how to get content by lines, you can chunk raw doc into pieces (like 5 line is a piece)
         String src_doc = "crash.java";
         int line_number = 0;
         while(reader!=EOF) {
             String line = reader.readLine();
             Document ld = new Document();
             ld.add(new Field("id", src_doc, true, true, false));
             ld.add(new Field("line", ""+line_number, true, true, false));
             ld.add(new Field("text", line.toString(), false, true, true));
             index_writer.addDocument(ld);
             line_number++;
         }
     */

    private void indexFile(File file) {
        try {
            String filePath = file.getCanonicalPath();
            String code = ReportUtils.getStockCodeFromFilePath(filePath);

            if(code2reports.containsKey(code)){
                if(!code2reports.get(code).reports.contains(filePath)){
                    if(!stockIndexed.containsKey(code)){
                        stockIndexed.put(code, code2reports.get(code));
                    }
                    code2reports.get(code).reports.add(filePath);
                } else {
                    return;
                }
            } else {
                StockReport report = new StockReport(code);
                report.reports.add(filePath);
                code2reports.put(code, report);
                stockIndexed.put(code, report);
            }

            PDDocument doc = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String content = pdfStripper.getText(doc);
            doc.close();

            Document document = new Document();
            document.add(new Field(LuceneConstants.CONTENTS, content, TextField.TYPE_STORED));
            document.add(new Field(LuceneConstants.FILE_NAME, file.getName(), TextField.TYPE_STORED));
            document.add(new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), TextField.TYPE_STORED));

            writer.updateDocument(new Term(LuceneConstants.FILE_PATH, file.getCanonicalPath()), document);

            fileCount++;

            if(fileCount % 50 == 1){
                logger.info("Indexing " + filePath);
            }
        } catch (IOException e) {
            logger.error("index report file failed.", e);
        }
    }

    /**
     * get all files in the data directory
     */
    public int createIndex(File dataDirPath) throws IOException {
        File[] files = dataDirPath.listFiles();

        for (File file : files) {
            if(file.isDirectory()){
                createIndex(file);
            } else if(!file.isHidden() && file.exists()
                    && file.canRead() && filter.accept(file)){
                indexFile(file);
            }
        }
        return fileCount;
    }

}
