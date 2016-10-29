package com.victor.midas.services;

import com.victor.midas.dao.StockReportDao;
import com.victor.midas.model.vo.StockReport;
import com.victor.midas.report.ReportUtils;
import com.victor.midas.services.worker.task.lucene.LuceneConstants;
import com.victor.midas.services.worker.task.lucene.ReportIndexer;
import com.victor.midas.services.worker.task.lucene.SingleSearcher;
import com.victor.midas.util.ModelConvertor;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class ReportService {

    private static final Logger logger = Logger.getLogger(ReportService.class);

    @Autowired
    public Environment environment;
    @Autowired
    public StockReportDao stockReportDao;

    public Map<String, List<String>> queryReports(String q) throws IOException, ParseException {
        IndexSearcher searcher = SingleSearcher.getInstance(environment.getProperty("Lucene.Stock.Report.Index.Path"));

        QueryParser parser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
        Query query = parser.parse(q);
        ScoreDoc[] hits = searcher.search(query, LuceneConstants.MAX_SEARCH).scoreDocs;

        Map<String, List<String>> stockCode2reports = new HashMap<>();

        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = searcher.doc(hits[i].doc);
            String code = ReportUtils.getStockCodeFromFilePath(hitDoc.get(LuceneConstants.FILE_PATH));
            if(stockCode2reports.containsKey(code)){
                stockCode2reports.get(code).add(hitDoc.get(LuceneConstants.FILE_NAME));
            } else {
                List<String> data = new ArrayList<>();
                data.add(hitDoc.get(LuceneConstants.FILE_NAME));
                stockCode2reports.put(code, data);
            }
        }
        return stockCode2reports;
    }

    public void indexReports() throws IOException {
        List<StockReport> reports = stockReportDao.queryAllStockReports();
        Map<String, StockReport> code2reports = ModelConvertor.toStockReportMap(reports);

        ReportIndexer indexer = new ReportIndexer(environment.getProperty("Lucene.Stock.Report.Index.Path"), code2reports);
        int numIndexed = indexer.createIndex(new File(environment.getProperty("MktDataLoader.Fundamental.cninfo")));
        indexer.close();

        stockReportDao.saveStockReports(indexer.stockIndexed.values());

        logger.info( numIndexed + " reports indexed...");
    }


}
