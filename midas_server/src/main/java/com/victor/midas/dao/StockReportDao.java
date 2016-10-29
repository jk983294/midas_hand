package com.victor.midas.dao;

import com.victor.midas.model.vo.StockReport;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Dao for index collection
 */
@Component
public class StockReportDao {
    private static final String COLLECTION_NAME = MidasConstants.STOCK_REPORTS_COLLECTION_NAME;

    private static final Logger logger = Logger.getLogger(StockReportDao.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<StockReport> queryAllStockReports(){
        return mongoTemplate.findAll(StockReport.class, COLLECTION_NAME);
    }

    public void saveStockReports(Collection<StockReport> reports){
        for (StockReport report : reports) {
            mongoTemplate.save(report, COLLECTION_NAME);
        }
    }

    /**
     * create task collection
     */
    public void createCollection(){
        if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
            logger.info("mongoTemplate create collection");
            mongoTemplate.createCollection(COLLECTION_NAME);
        }
    }

    /**
     * delete task collection, means that all task documents will be deleted
     */
    public void deleteCollection(){
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            logger.info("drop stock Collection");
            mongoTemplate.dropCollection(COLLECTION_NAME);
        }
    }

}
