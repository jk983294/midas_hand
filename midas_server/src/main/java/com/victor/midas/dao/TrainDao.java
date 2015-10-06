package com.victor.midas.dao;

import com.victor.midas.model.vo.TrainResult;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TrainDao {
    private static final String COLLECTION_NAME = MidasConstants.TRAIN_COLLECTION_NAME;

    private static final Logger logger = Logger.getLogger(TrainDao.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SequenceDao sequenceDao;

    public TrainResult queryTrainResult(long trainId){
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(trainId)), TrainResult.class, COLLECTION_NAME);
    }

    public List<TrainResult> queryAllTrainResult(){
        return mongoTemplate.findAll(TrainResult.class, COLLECTION_NAME);
    }


    public TrainResult queryLastTrainResult() throws MidasException {
        long trainId = sequenceDao.getLatestSequenceId(MidasConstants.SEQUENCE_DOCUMENT_TRAIN);
        return queryTrainResult(trainId);
    }

    /**
     * save the task to DB, the task Id will be populated automatically
     * next time, if save again, it will saved by that Id
     */
    public void saveTrainResult(Collection<TrainResult> trainResults) throws MidasException {
        for (TrainResult trainResult : trainResults) {
            saveTrainResult(trainResult);
        }
    }
    public void saveTrainResult(TrainResult trainResult) throws MidasException {
        trainResult.setTrainId(sequenceDao.getNextSequenceId(MidasConstants.SEQUENCE_DOCUMENT_TRAIN));
        mongoTemplate.save(trainResult, COLLECTION_NAME);
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
