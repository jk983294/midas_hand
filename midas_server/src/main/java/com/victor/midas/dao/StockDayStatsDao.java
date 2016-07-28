package com.victor.midas.dao;

import com.victor.midas.model.vo.StockDayStats;
import com.victor.midas.util.MidasConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Dao for day stats collection
 */
@Component
public class StockDayStatsDao {
    private final String COLLECTION_NAME = MidasConstants.STOCK_DAY_STATS_COLLECTION_NAME;

    private static final Logger logger = Logger.getLogger(StockDayStatsDao.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * save StockDayStats, must remove old docs
     */
    public void save(List<StockDayStats> dayStatsList){
        for (StockDayStats dayStats : dayStatsList){
            save(dayStats);
        }
    }
    public void save(StockDayStats dayStats){
        mongoTemplate.save(dayStats, COLLECTION_NAME);
    }

    /**
     * query by its date
     */
    public StockDayStats queryByCob(Integer date){
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(date)), StockDayStats.class, COLLECTION_NAME);
    }

    public List<StockDayStats> queryAll(){
        List<StockDayStats> records = mongoTemplate.findAll(StockDayStats.class, COLLECTION_NAME);
        Collections.sort(records);
        return records;
    }


    public int getCount() {
        return (int) mongoTemplate.count( new Query(), COLLECTION_NAME);
    }

    /**
     * create collection
     */
    public void createCollection(){
        if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
            logger.info("mongoTemplate create collection " + COLLECTION_NAME);
            mongoTemplate.createCollection(COLLECTION_NAME);
        }
    }

    /**
     * delete collection, means that all documents will be deleted
     */
    public void deleteCollection(){
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            logger.info("drop Collection " + COLLECTION_NAME);
            mongoTemplate.dropCollection(COLLECTION_NAME);
        }
    }
}
