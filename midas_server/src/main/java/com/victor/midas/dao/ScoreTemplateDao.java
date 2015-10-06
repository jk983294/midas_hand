package com.victor.midas.dao;

import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.util.MidasConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Dao for score collection
 */
@Component
public class ScoreTemplateDao {

    private static final Logger logger = Logger.getLogger(ScoreTemplateDao.class);

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * save StockScoreRecord, must remove old docs
     */
    public void save(String collectionName, List<StockScoreRecord> stocks){
        for (StockScoreRecord StockScoreRecord : stocks){
            save(collectionName, StockScoreRecord);
        }
    }
    public void save(String collectionName, StockScoreRecord StockScoreRecord){
        mongoTemplate.save(StockScoreRecord, collectionName);
    }

    /**
     * get latest N StockScoreRecord
     */
    public List<StockScoreRecord> queryLastStockScoreRecord(String collectionName, int n){
        int docCnt = getCount(collectionName);
        if(docCnt > n){
            Query query =new Query().withHint("_id_").limit(n).skip(docCnt - n);
            List<StockScoreRecord> records = mongoTemplate.find(query, StockScoreRecord.class, collectionName);
            Collections.reverse(records);
            return records;
        } else {
            return queryAll(collectionName);
        }
    }

    public List<StockScoreRecord> queryStockScoreRecordByRange(String collectionName, int cobFrom, int cobTo){
        Query query =new Query();
        query.addCriteria(Criteria.where("cob").gte(cobFrom).andOperator(Criteria.where("cob").lte(cobTo)));
        query.with(new Sort(Sort.Direction.DESC, "cob"));
        List<StockScoreRecord> records = mongoTemplate.find(query, StockScoreRecord.class, collectionName);
        return records;
    }

    /**
     * query one day focus by its date
     */
    public StockScoreRecord queryByName(String collectionName, Integer date){
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(date)), StockScoreRecord.class, collectionName);
    }

    public List<StockScoreRecord> queryAll(String collectionName){
        return mongoTemplate.findAll(StockScoreRecord.class, collectionName);
    }


    public int getCount(String collectionName) {
        return (int) mongoTemplate.count( new Query(), collectionName);
    }


    /**
     * create collection
     */
    public void createCollection(String collectionName){
        if (!mongoTemplate.collectionExists(collectionName)) {
            logger.info("mongoTemplate create collection " + collectionName);
            mongoTemplate.createCollection(collectionName);
        }
    }

    /**
     * delete collection, means that all documents will be deleted
     */
    public void deleteCollection(String collectionName){
        if (mongoTemplate.collectionExists(collectionName)) {
            logger.info("drop Collection " + collectionName);
            mongoTemplate.dropCollection(collectionName);
        }
    }
}
