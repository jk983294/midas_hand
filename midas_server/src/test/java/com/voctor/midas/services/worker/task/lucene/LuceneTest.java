package com.voctor.midas.services.worker.task.lucene;

import com.victor.midas.services.worker.task.lucene.LuceneConstants;
import com.victor.midas.services.worker.task.lucene.ReportIndexer;
import com.victor.utilities.utils.OsHelper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;


public class LuceneTest {

    String indexDir = OsHelper.getPathByOs("/home/kun/", "F:\\", "Data\\dummy\\ReportSearch");
    String dataDir = OsHelper.getPathByOs("/home/kun/", "F:\\", "Data\\MktData\\fundamental\\cninfo\\000001");

    @Ignore
    @Test
    public void indexerTest() throws IOException {
        ReportIndexer indexer = new ReportIndexer(indexDir, new HashMap<>());
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(new File(dataDir));
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: " + (endTime - startTime) + " ms");
    }

    @Ignore
    @Test
    public void queryTest() throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
        DirectoryReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
        Query query = parser.parse("智能车");
        ScoreDoc[] hits = searcher.search(query, LuceneConstants.MAX_SEARCH).scoreDocs;

        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = searcher.doc(hits[i].doc);
            System.out.println("File: " + hitDoc.get(LuceneConstants.FILE_PATH));
        }

        reader.close();
        indexDirectory.close();
    }
}
