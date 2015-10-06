package com.victor.midas.dao;

import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Dao for index collection
 */
@Component
public class StockDao {
    private static final String COLLECTION_NAME = MidasConstants.STOCK_COLLECTION_NAME;

    private static final Logger logger = Logger.getLogger(StockDao.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Cacheable(value="stocks", key="#stockName")
    public StockVo queryStock(String stockName){
        logger.info("fetch stock " + stockName);
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(stockName)), StockVo.class, COLLECTION_NAME);
    }

    public List<StockVo> queryAllStock(){
        return mongoTemplate.findAll(StockVo.class, COLLECTION_NAME);
    }

    /**
     * save the task to DB, the task Id will be populated automatically
     * next time, if save again, it will saved by that Id
     */
    @CacheEvict(value="stocks", allEntries=true)
    public void saveStock(Collection<StockVo> stocks){
        for (StockVo stock : stocks) {
            mongoTemplate.save(stock, COLLECTION_NAME);
        }
    }

    @CacheEvict(value="stocks", key="#stock.stockName")
    private void updateStock(StockVo stock){
        mongoTemplate.save(stock, COLLECTION_NAME);
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
