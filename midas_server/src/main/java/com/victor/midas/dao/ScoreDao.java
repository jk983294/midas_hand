package com.victor.midas.dao;

import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.util.MidasConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Dao for score collection
 */
@Component
public class ScoreDao {
    private final String COLLECTION_NAME = MidasConstants.SCORE_COLLECTION_NAME;

    private static final Logger logger = Logger.getLogger(ScoreDao.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ScoreTemplateDao scoreTemplateDao;

    /**
     * save StockScoreRecord, must remove old docs
     */
    public void save(List<StockScoreRecord> stocks){
        scoreTemplateDao.save(COLLECTION_NAME, stocks);
    }
    public void save(StockScoreRecord StockScoreRecord){
        scoreTemplateDao.save(COLLECTION_NAME, StockScoreRecord);
    }

    /**
     * get latest N StockScoreRecord
     */
    public List<StockScoreRecord> queryLastStockScoreRecord(int n){
        return filter(scoreTemplateDao.queryLastStockScoreRecord(COLLECTION_NAME, n));
    }

    public List<StockScoreRecord> queryStockScoreRecordByRange(int cobFrom, int cobTo){
        return filter(scoreTemplateDao.queryStockScoreRecordByRange(COLLECTION_NAME, cobFrom, cobTo));
    }

    /**
     * query one day focus by its date
     */
    public StockScoreRecord queryByName(Integer date){
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(date)), StockScoreRecord.class, COLLECTION_NAME);
    }

    public List<StockScoreRecord> queryAll(){
        return filter(mongoTemplate.findAll(StockScoreRecord.class, COLLECTION_NAME));
    }


    public int getCount() {
        return (int) mongoTemplate.count( new Query(), COLLECTION_NAME);
    }


    public List<StockScoreRecord> filter(List<StockScoreRecord> records){
        List<StockScoreRecord> filtered = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(records)){
            for(StockScoreRecord record : records){
                if(record.getRecords().size() > 0){
                    filtered.add(record);
                }
            }
        }
        return filtered;
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
