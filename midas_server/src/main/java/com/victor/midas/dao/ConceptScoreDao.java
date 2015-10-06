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
public class ConceptScoreDao {
    private final String COLLECTION_NAME = MidasConstants.CONCEPT_SCORE_COLLECTION_NAME;

    private static final Logger logger = Logger.getLogger(DayFocusDao.class);

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * save StockScoreRecord, must remove old docs
     */
    public void save(List<StockScoreRecord> stocks){
        for (StockScoreRecord StockScoreRecord : stocks){
            save(StockScoreRecord);
        }
    }
    public void save(StockScoreRecord StockScoreRecord){
        mongoTemplate.save(StockScoreRecord, COLLECTION_NAME);
    }

    /**
     * get latest N StockScoreRecord
     */
    public List<StockScoreRecord> queryLastStockScoreRecord(int n){
        int docCnt = getCount();
        if(docCnt > n){
            Query query =new Query().withHint("_id_").limit(n).skip(docCnt - n);
            List<StockScoreRecord> records = mongoTemplate.find(query, StockScoreRecord.class, COLLECTION_NAME);
            Collections.reverse(records);
            return records;
        } else {
            return queryAll();
        }
    }

    public List<StockScoreRecord> queryStockScoreRecordByRange(int cobFrom, int cobTo){
        Query query =new Query();
        query.addCriteria(Criteria.where("cob").gte(cobFrom).andOperator(Criteria.where("cob").lte(cobTo)));
        query.with(new Sort(Sort.Direction.DESC, "cob"));
        List<StockScoreRecord> records = mongoTemplate.find(query, StockScoreRecord.class, COLLECTION_NAME);
        return records;
    }

    /**
     * query one day focus by its date
     */
    public StockScoreRecord queryByName(Integer date){
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(date)), StockScoreRecord.class, COLLECTION_NAME);
    }

    public List<StockScoreRecord> queryAll(){
        return mongoTemplate.findAll(StockScoreRecord.class, COLLECTION_NAME);
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
