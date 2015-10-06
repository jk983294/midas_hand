package com.victor.midas.dao;

import com.victor.midas.model.db.DayFocusDb;
import com.victor.midas.model.db.StockInfoDb;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DayFocusDao {
	private final String COLLECTION_NAME = MidasConstants.DAY_FOCUS_COLLECTION_NAME;
	
	private static final Logger logger = Logger.getLogger(DayFocusDao.class);
	
	
	@Autowired  
    MongoTemplate mongoTemplate; 

	/**
	 * save day focus, must remove old docs
	 */
    public void saveDayFocus(List<DayFocusDb> stocks){
        deleteCollection();
        createCollection();
        for (DayFocusDb dayFocusDb : stocks){
            saveDayFocus(dayFocusDb);
        }
    }
	public void saveDayFocus(DayFocusDb dayFocusDb){
		mongoTemplate.save(dayFocusDb, COLLECTION_NAME);
	}

    /**
     * get latest N DayFocusDb
     */
    public List<DayFocusDb> queryLastDayFocus(int n){
        int docCnt = getFocusCount();
        if(docCnt > n){
            Query query =new Query().withHint("_id_").limit(n).skip(docCnt - n);
            return mongoTemplate.find(query, DayFocusDb.class, COLLECTION_NAME);
        } else {
            return queryAllDayFocus();
        }
    }
	
	/**
	 * query one day focus by its date
	 */
	public DayFocusDb queryByName(Integer date){
		return mongoTemplate.findOne(new Query(Criteria.where("_id").is(date)), DayFocusDb.class, COLLECTION_NAME);
    }

    public List<DayFocusDb> queryAllDayFocus(){
        return mongoTemplate.findAll(DayFocusDb.class, COLLECTION_NAME);
    }

	
	public int getFocusCount() {
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
            logger.info("drop task Collection " + COLLECTION_NAME);
            mongoTemplate.dropCollection(COLLECTION_NAME);
        }
    }
}
